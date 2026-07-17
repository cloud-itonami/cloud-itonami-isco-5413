(ns facilityops.store
  "SSoT for the ISCO-08 5413 prison guards facility/equipment/scheduling
  documentation / administrative-logistics coordination actor (itonami
  actor pattern, ADR-2607121000 / CLAUDE.md Actors section; README's
  'Robotics premise' — a facility documentation and logistics robot
  performs equipment/security-system readiness data entry, staff
  shift/training scheduling and non-weapon facility-equipment supply
  coordination under this advisor/governor pair, which never dispatches
  hardware itself and NEVER exercises, simulates exercising, or
  proposes exercising ANY use-of-force, physical-restraint,
  disciplinary-sanction (solitary confinement, privilege revocation,
  etc.) or movement/confinement-condition-restriction authority over an
  incarcerated person — every one of those capabilities is a
  permanently out-of-scope, structurally absent op; this actor cannot
  apply a physical restraint, use force, impose a disciplinary
  sanction, or restrict an incarcerated person's movement or
  confinement conditions, no matter how confident the advisor is or
  how a human resumes an interrupted run). Modeled on
  cloud-itonami-isco-3355's caseadmin.store (closed op allowlist +
  independently-registered-AND-verified provenance for both the
  proposing officer and the referenced facility), itself modeled on
  cloud-itonami-isco-3313's accountingsupport.store.

  Domain:

    officer   — a registered corrections officer/facility staff member
                {:officer-id :name :facility-id :verified? boolean}.
                Independently registered/verified identity, never
                trusted from the proposal alone (\"officer/facility
                record must be independently verified/registered
                before any action\"). This actor never determines this
                officer's custodial decisions — it only logs,
                schedules and flags administrative/logistics records
                on the officer's behalf.
    facility  — a registered correctional facility
                {:facility-id :max-supply-cost number :verified?
                boolean}. Independently registered/verified, never
                trusted from the proposal alone.
                `:max-supply-cost` documents the registered facility
                supply ceiling for future per-facility threshold use;
                the shared `:coordinate-supply-order` cost above which
                always escalates to a human is
                `facilityops.governor/supply-cost-escalation-threshold`
                — NOT a hard block by itself, a non-weapon supply order
                over budget just needs sign-off, it is not itself
                unsafe (a weapon/restraint-device item is instead
                always a HARD block regardless of cost, see
                `facilityops.governor/weapon-restraint-item?`).
    record    — a committed operating record (equipment/security-system
                readiness log entry, staff shift/training scheduling
                proposal, facility-concern flag, or non-weapon
                supply-order coordination proposal) — written ONLY via
                commit-record!. A committed record is NEVER a
                use-of-force action, a physical restraint, a
                disciplinary sanction, or a restriction of an
                incarcerated person's movement or confinement
                conditions — this actor documents and coordinates
                logistics, it never exercises custodial authority.
    ledger    — append-only audit trail, commit or hold.")

(defprotocol Store
  (officer [s officer-id])
  (facility [s facility-id])
  (records-of [s facility-id])
  (ledger [s])
  (register-officer! [s o])
  (register-facility! [s f])
  (commit-record! [s record])
  (append-ledger! [s fact]))

(defrecord MemStore [a]
  Store
  (officer [_ officer-id] (get-in @a [:officers officer-id]))
  (facility [_ facility-id] (get-in @a [:facilities facility-id]))
  (records-of [_ facility-id] (filter #(= facility-id (:facility-id %)) (:records @a)))
  (ledger [_] (:ledger @a))
  (register-officer! [s o]
    (swap! a assoc-in [:officers (:officer-id o)] o) s)
  (register-facility! [s f]
    (swap! a assoc-in [:facilities (:facility-id f)] f) s)
  (commit-record! [s record]
    (swap! a update :records (fnil conj []) record) s)
  (append-ledger! [s fact]
    (swap! a update :ledger (fnil conj []) fact) s))

(defn mem-store
  ([] (mem-store {}))
  ([seed] (->MemStore (atom (merge {:officers {} :facilities {} :records [] :ledger []}
                                   seed)))))
