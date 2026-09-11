(ns facilityops.governor
  "FacilityOpsGovernor — the independent safety/traceability layer named
  in this repository's README/business-model.md, gating every
  documentation/logistics-coordination operation an advisor may
  propose. The governor never dispatches hardware itself and NEVER
  lets a proposal exercise, simulate exercising, or propose exercising
  ANY use-of-force, physical-restraint, disciplinary-sanction
  (solitary confinement, privilege revocation, etc.), or
  movement/confinement-condition-restriction authority over an
  incarcerated person — every one of those is permanently out of scope
  for this actor, not merely gated behind escalation. Incarcerated
  persons are a captive population with limited ability to resist
  wrongful treatment, so this guardrail is stricter than a normal
  person-facing-service escalation gate (ADR-2607152500 Wave4
  guardrail lineage): the closed op allowlist never even contains a
  custodial-authority op to begin with. Modeled on
  cloud-itonami-isco-3355's caseadmin.governor, with the same closed
  proposal-op allowlist + content-based scope-exclusion shape, adapted
  to this vertical's use-of-force/restraint/disciplinary/confinement
  guardrail.

  HARD invariants (:hard? true, ALWAYS :hold, never overridable):
    1. officer provenance      — the proposing corrections officer
                                record must be independently
                                registered AND verified before ANY
                                proposal can commit or escalate. Never
                                trusts the proposal's own claim of who
                                the officer is.
    2. no-actuation             — proposal :effect must be :propose
                                (the governor never dispatches
                                hardware and never itself performs a
                                custodial or facility action; it only
                                gates what the advisor may commit).
    3. closed op allowlist      — the proposal's :op must be one of
                                the four ops this actor is scoped to
                                (`closed-op-allowlist` below). This is
                                the STRUCTURAL guarantee: no op that
                                resembles applying physical restraint,
                                using force, imposing a disciplinary
                                sanction, or restricting an
                                incarcerated person's movement or
                                confinement conditions exists anywhere
                                in this allowlist — such a proposal
                                cannot even reach a check, let alone
                                pass one. Any :op outside the allowlist
                                is a HARD, PERMANENT block.
    4. facility-record basis    — a proposal for `:log-facility-record`,
                                `:schedule-staff-operation` or
                                `:coordinate-supply-order` must cite a
                                REGISTERED AND VERIFIED facility that
                                the proposing officer is themselves
                                assigned to (`:unknown-facility` /
                                `:facility-unverified` /
                                `:facility-mismatch`).
                                `:flag-facility-concern` does NOT
                                require an existing facility record (it
                                is the channel by which a concern is
                                surfaced before facility paperwork is
                                complete).
    5. incarcerated-person-conclusion forbidden — `:log-facility-record`
                                is non-custodial facility/equipment
                                readiness metadata ONLY (equipment ID,
                                security-system, condition, timestamp).
                                Any proposal carrying a field that
                                names, identifies or draws a conclusion
                                about a specific incarcerated person
                                (`log-record-forbidden-keys` below —
                                e.g. `:incarcerated-person-id`,
                                `:disciplinary-conclusion`) is a HARD,
                                PERMANENT block — this actor never
                                records anything about a specific
                                incarcerated person's conduct, only the
                                physical/administrative condition of
                                facility equipment and systems.
    6. staff-operation-content forbidden — `:schedule-staff-operation`
                                is shift/training scheduling logistics
                                ONLY. Any proposal carrying
                                operational/tactical content
                                (`staff-operation-forbidden-keys` below
                                — e.g. `:force-plan`, `:restraint-plan`,
                                `:extraction-plan`) is a HARD, PERMANENT
                                block — this actor never plans, records
                                or authorizes a use-of-force, restraint
                                or cell-extraction operation, only
                                administrative shift/training
                                calendars.
    7. weapon/restraint-device supply forbidden — `:coordinate-supply-order`
                                is non-weapon facility-equipment
                                procurement ONLY. Any proposal whose
                                `:item`/`:item-category` names a
                                weapon or restraint device
                                (`weapon-restraint-item-terms` below —
                                e.g. firearm, taser, baton, handcuffs,
                                restraint chair, pepper spray) is a
                                HARD, PERMANENT block, independent of
                                and in addition to the cost-threshold
                                escalation (#9 below) — this is not
                                merely gated by cost, it never
                                auto-commits or escalates to \"just
                                sign off\", it is unconditionally
                                refused.
    8. scope exclusion           — independent, DEFENSE-IN-DEPTH layer
                                on top of #3/#5/#6/#7: even for an
                                otherwise-allowed op, any proposal
                                whose free text (`:rationale` or
                                `:note`) names a finalization/execution
                                ACTION for applying physical restraint,
                                using force, imposing a disciplinary
                                sanction, or restricting an incarcerated
                                person's movement/confinement conditions
                                (`scope-excluded-terms` below) is a
                                HARD, PERMANENT block, evaluated
                                unconditionally on content. This actor
                                never exercises custodial-authority or
                                enforcement authority — it only
                                documents facility/equipment readiness
                                and coordinates officer logistics.
  ESCALATION invariants (:escalate? true, ALWAYS human sign-off — these
  are :high/:safety-critical regardless of confidence):
    9. :op :flag-facility-concern (surfacing an equipment/
                                security-system/staffing concern —
                                ALWAYS requires human corrections
                                officer/supervisor review; it is never
                                auto-resolved and never appears in any
                                phase's auto-commit set; this is the
                                ONLY path by which a facility
                                observation may be surfaced, and the
                                robot's role ends at \"here is the
                                readiness/concern data\" — never \"here
                                is what I recommend doing about an
                                incarcerated person\").
    10. an above-threshold :coordinate-supply-order (non-weapon
                                facility-equipment procurement above
                                `supply-cost-escalation-threshold`
                                always needs human sign-off, regardless
                                of confidence — this is an escalation,
                                NOT a hard block, since an over-budget
                                *non-weapon* supply request is not
                                itself unsafe; a weapon/restraint-device
                                item is instead HARD-blocked per #7
                                regardless of cost).
    11. low confidence (< `confidence-floor`)."
  (:require [kotoba.lang.text :as str]
            [facilityops.store :as store]))

(def confidence-floor 0.6)

;; Non-weapon facility-equipment supply orders at or below this
;; estimated cost may be auto-commit-eligible (subject to confidence);
;; above it, ALWAYS escalates to a human regardless of confidence. A
;; weapon/restraint-device item is HARD-blocked (rule #7 above) no
;; matter its cost — this threshold only ever gates ordinary equipment.
(def supply-cost-escalation-threshold 500)

;; The closed proposal-op allowlist. This governor NEVER allows any op
;; outside this set to commit or even escalate — an op outside this
;; set is a HARD, permanent block (see `hard-violations`
;; :op-not-allowed below), not merely un-auto-committable. This is a
;; facility/equipment/scheduling documentation/administrative-logistics
;; coordination robot ONLY: it has NO op, anywhere in this allowlist,
;; that resembles applying physical restraint, using force, imposing a
;; disciplinary sanction (solitary confinement, privilege revocation,
;; etc.), or restricting an incarcerated person's movement/access/
;; confinement conditions. Those capabilities are structurally absent,
;; not gated.
(def closed-op-allowlist
  #{:log-facility-record :schedule-staff-operation
    :flag-facility-concern :coordinate-supply-order})

;; :flag-facility-concern always escalates to a human — never
;; auto-commit-eligible at any phase. It is the ONLY channel through
;; which a facility observation may be surfaced.
(def ^:private always-escalate-ops #{:flag-facility-concern})

;; Ops that outside observers might expect a "prison guard actor" to
;; have — named here explicitly (in addition to the closed-allowlist
;; check above) so the exclusion reads as an intentional, documented
;; scope boundary rather than an incidental unknown op. None of these
;; are ever defined as a real op anywhere in this codebase; they exist
;; ONLY as negative-test fixtures proving `closed-op-allowlist` rejects
;; them.
(def scope-excluded-ops
  #{:apply-physical-restraint :use-force :authorize-use-of-force
    :impose-disciplinary-sanction :impose-solitary-confinement
    :place-in-segregation :revoke-privileges :restrict-movement
    :restrict-inmate-movement :determine-confinement-conditions
    :authorize-cell-extraction :execute-cell-extraction})

;; log-facility-record is non-custodial facility/equipment readiness
;; metadata ONLY. A proposal carrying any of these keys is smuggling
;; an identification of, or a conclusion about, a specific
;; incarcerated person into what must remain a pure equipment/
;; security-system condition log.
(def log-record-forbidden-keys
  #{:incarcerated-person-id :inmate-id :person-of-concern :conclusion
    :finding :disciplinary-conclusion :disciplinary-finding
    :behavioral-assessment :behavioral-determination :incident-conclusion})

;; schedule-staff-operation is shift/training scheduling logistics
;; ONLY. A proposal carrying any of these keys is smuggling an
;; operational/tactical use-of-force, restraint or extraction plan
;; into what must remain pure administrative scheduling.
(def staff-operation-forbidden-keys
  #{:force-plan :restraint-plan :extraction-plan :tactical-plan
    :use-of-force-authorization :disciplinary-directive
    :cell-extraction-plan})

;; coordinate-supply-order is non-weapon facility-equipment
;; procurement ONLY. A proposal whose :item or :item-category matches
;; (case-insensitively, substring) any of these terms names a weapon
;; or physical-restraint device and is a HARD, PERMANENT block
;; regardless of cost — never merely gated by
;; `supply-cost-escalation-threshold`.
(def weapon-restraint-item-terms
  ["firearm" "gun" "pistol" "rifle" "shotgun" "taser" "stun gun"
   "stun belt" "conductive energy weapon" "baton" "handcuff"
   "leg iron" "shackle" "restraint chair" "restraint device"
   "waist chain" "pepper spray" "oc spray" "chemical agent"
   "less lethal" "flash bang" "rubber bullet" "beanbag round"
   "capture net" "ammunition"])

;; Scope-exclusion terms, phrased as the FINALIZATION/EXECUTION ACTION
;; (never a bare noun like "restraint", "sanction" or "privileges"
;; alone) — a known self-tripping bug class in this fleet: a bare-noun
;; term list can accidentally match inside the mock advisor's own
;; default rationale text for a legitimate, allowed proposal, causing
;; the actor to self-block on its own happy path. This advisor's
;; default rationale template is "documented <op> for facility <id>",
;; which never contains any of these full action phrases. See
;; `facilityops.governor-test/
;; default-mock-advisor-proposals-never-self-trip-scope-exclusion`.
(def scope-excluded-terms
  ["apply the physical restraint" "applied the physical restraint"
   "use force on the inmate" "used force on the inmate"
   "use force on the incarcerated person" "used force on the incarcerated person"
   "authorize the use of force" "authorized the use of force"
   "impose the disciplinary sanction" "imposed the disciplinary sanction"
   "impose solitary confinement" "imposed solitary confinement"
   "place in solitary confinement" "placed in solitary confinement"
   "place in segregation" "placed in segregation"
   "revoke the privileges" "revoked the privileges"
   "revoke inmate privileges" "revoked inmate privileges"
   "restrict movement privileges" "restricted movement privileges"
   "restrict the inmate's movement" "restricted the inmate's movement"
   "authorize the cell extraction" "authorized the cell extraction"
   "execute the cell extraction" "executed the cell extraction"
   "determine the confinement conditions" "determined the confinement conditions"
   "change the confinement conditions" "changed the confinement conditions"
   "身体拘束を実施した" "有形力を行使した" "実力を行使した" "懲罰を科した"
   "独居拘禁を科した" "隔離拘禁を科した" "特権を剥奪した" "移動を制限した"
   "拘禁条件を変更した"])

(defn out-of-scope?
  "True if any free-text field on `proposal` (:rationale or :note)
  contains a scope-excluded finalization/execution phrase for applying
  physical restraint, using force, imposing a disciplinary sanction,
  or restricting an incarcerated person's movement/confinement
  conditions."
  [proposal]
  (let [text (str (:rationale proposal) " " (:note proposal))]
    (boolean (some #(str/includes? text %) scope-excluded-terms))))

(defn- forbidden-keys-present [proposal forbidden-keys]
  (seq (filter #(contains? proposal %) forbidden-keys)))

(defn- normalize-item-text
  "Lower-case and fold hyphens/underscores to spaces, so a keyword- or
  slug-shaped :item-category (e.g. `:restraint-device`,
  \"restraint_device\") matches the same space-separated
  `weapon-restraint-item-terms` phrase as free-text prose (\"restraint
  device\")."
  [s]
  (-> (str s) str/lower (str/replace #"[-_]+" " ")))

(defn weapon-restraint-item?
  "True if `proposal`'s :item or :item-category names a weapon or
  physical-restraint device (case- and separator-insensitive substring
  match against `weapon-restraint-item-terms`)."
  [proposal]
  (let [text (str (normalize-item-text (:item proposal)) " " (normalize-item-text (:item-category proposal)))]
    (boolean (some #(str/includes? text %) weapon-restraint-item-terms))))

(def ^:private facility-required-ops
  #{:log-facility-record :schedule-staff-operation :coordinate-supply-order})

(defn- hard-violations [{:keys [proposal]} officer-record facility-record]
  (let [{:keys [op facility-id]} proposal
        needs-facility? (contains? facility-required-ops op)]
    (cond-> []
      (not= :propose (:effect proposal))
      (conj {:rule :no-actuation :detail "effect は :propose のみ許可（governor は施設/装備の直接操作を実行しない）"})

      (not (contains? closed-op-allowlist op))
      (conj {:rule :op-not-allowed
             :detail "closed allowlist 外の op（有形力の行使・身体拘束・懲罰・移動/拘禁条件の制限を含む一切の身柄拘束権限は許可されない）"})

      (nil? officer-record)
      (conj {:rule :unknown-officer :detail "未登録 officer への提案は不可"})

      (and officer-record (not (:verified? officer-record)))
      (conj {:rule :officer-unverified :detail "未検証 officer への提案は不可（登録のみでは不十分）"})

      (and needs-facility? (nil? facility-id))
      (conj {:rule :missing-facility-id :detail "この op には facility-id が必須"})

      (and needs-facility? facility-id (nil? facility-record))
      (conj {:rule :unknown-facility :detail "未登録 facility への提案は不可"})

      (and needs-facility? facility-record (not (:verified? facility-record)))
      (conj {:rule :facility-unverified :detail "未検証 facility への提案は不可（登録のみでは不十分）"})

      (and needs-facility? facility-record officer-record
           (not= (:facility-id facility-record) (:facility-id officer-record)))
      (conj {:rule :facility-mismatch :detail "facility が officer の配属先と異なる"})

      (and (= :log-facility-record op) (seq (forbidden-keys-present proposal log-record-forbidden-keys)))
      (conj {:rule :incarcerated-person-conclusion-forbidden
             :detail "log-facility-record は施設/装備の物理的な状態記録のみ — 特定の被収容者に関する識別・結論は永久に禁止"})

      (and (= :schedule-staff-operation op) (seq (forbidden-keys-present proposal staff-operation-forbidden-keys)))
      (conj {:rule :staff-operation-content-forbidden
             :detail "schedule-staff-operation は勤務/研修の日程調整のみ — 有形力/身体拘束/独居拘禁の作戦内容の記録は永久に禁止"})

      (and (= :coordinate-supply-order op) (weapon-restraint-item? proposal))
      (conj {:rule :weapon-restraint-item-forbidden
             :detail "武器・拘束具の調達提案は金額に関わらず恒久的に禁止（コスト閾値でのgateではなくhard block）"})

      (out-of-scope? proposal)
      (conj {:rule :scope-excluded
             :detail "有形力の行使・身体拘束の実施・懲罰の賦課・移動/拘禁条件の制限を直接確定する提案は恒久的に許可されない（このactorは文書化とロジスティクス調整のみを行う）"}))))

(defn check
  "Assess a proposal against `request`/`context`/`proposal` and a
  `store` implementing `facilityops.store/Store`. Pure — never mutates
  the store, never applies physical restraint, never uses force, never
  imposes a disciplinary sanction, never restricts an incarcerated
  person's movement or confinement conditions."
  [_request _context proposal store]
  (let [officer-record (some->> (:officer-id proposal) (store/officer store))
        facility-record (some->> (:facility-id proposal) (store/facility store))
        hard (hard-violations {:proposal proposal} officer-record facility-record)
        hard? (boolean (seq hard))
        conf (or (:confidence proposal) 0.0)
        low? (< conf confidence-floor)
        always-risky? (contains? always-escalate-ops (:op proposal))
        over-threshold-supply-order?
        (and (= :coordinate-supply-order (:op proposal))
             (number? (:cost proposal))
             (> (:cost proposal) supply-cost-escalation-threshold))]
    {:ok? (and (not hard?) (not low?) (not always-risky?) (not over-threshold-supply-order?))
     :violations hard
     :confidence conf
     :hard? hard?
     :escalate? (and (not hard?) (or low? always-risky? over-threshold-supply-order?))}))
