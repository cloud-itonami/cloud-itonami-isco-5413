# cloud-itonami-isco-5413

Open Occupation Blueprint for **ISCO-08 5413**: Prison Guards.

This repository designs a forkable OSS business for a correctional
facility equipment/scheduling documentation and administrative-logistics
coordination practice: a facility documentation and logistics robot
manages equipment/security-system readiness records, staff
shift/training scheduling and non-weapon facility-equipment
procurement coordination under a governor-gated actor — and
structurally **never** applies a physical restraint, uses force,
imposes a disciplinary sanction, or restricts an incarcerated person's
movement, access or confinement conditions itself.

## This actor has no use-of-force, restraint, disciplinary, or confinement-condition authority

Prison Guards have direct legal authority to physically restrain
incarcerated persons, use force (including in cell-extraction and
use-of-force incidents), control movement/privileges, and impose
disciplinary sanctions over a captive population with limited ability
to resist wrongful treatment — making wrong autonomous action here
especially severe. **This actor is a facility/equipment/scheduling
documentation/administrative-logistics coordination robot ONLY.** It
has NO op, anywhere in its allowlist, that resembles applying a
physical restraint, using force, imposing a disciplinary sanction
(solitary confinement, privilege revocation, etc.), or restricting an
incarcerated person's movement, access or confinement conditions.
These are **structurally absent from the closed op-allowlist
entirely**, not merely gated behind escalation — under any
circumstance, at any confidence level, in any phase, with no
exception whatsoever. Any observation the robot logs that suggests a
facility needs human attention is surfaced ONLY via an
always-escalating `:flag-facility-concern` op that a human corrections
officer/supervisor reviews and acts on entirely themselves. This
mirrors the Wave4 person-facing-service safety guardrail
(ADR-2607152500) and is, if anything, a stricter application of it:
decisions directly touching an incarcerated person's liberty, bodily
safety or conditions of confinement always exclude the closed op
allowlist and always escalate. The robot's role ends at "here is the
facility readiness/concern data" — never "here is what I recommend
doing about an incarcerated person."

**Maturity: `:implemented`.** `src/facilityops/` implements the
`FacilityOpsActor` as a `langgraph.graph/state-graph`
(`facilityops.actor`) wired to a `Facility Ops Advisor`
(`facilityops.advisor`) and an independent `FacilityOpsGovernor`
(`facilityops.governor`), following the itonami actor pattern
(ADR-2607121000): `:intake -> :advise -> :govern -> :decide -+-> :commit
(:ok?) +-> :request-approval (:escalate?, human-in-the-loop interrupt)
+-> :hold (:hard?)`. 51 tests / 172 assertions green (`kbb -M:test`).

HARD invariants (always hold, never overridable): officer provenance
(a proposal must resolve to an independently registered AND verified
corrections officer record), a closed four-op proposal allowlist (any
op outside it — including anything that would apply a physical
restraint, use force, impose a disciplinary sanction, or restrict an
incarcerated person's movement or confinement conditions — is a
permanent HARD block, because no such op exists in the allowlist to
begin with), no-actuation (`:effect` must be `:propose`), a
registered-and-verified facility basis matching the officer's own
assignment (for the three ops that reference one), an
incarcerated-person-conclusion-forbidden check (`:log-facility-record`
may only carry physical equipment/security-system condition metadata,
never an identification of or conclusion about a specific incarcerated
person), a staff-operation-content-forbidden check
(`:schedule-staff-operation` may only carry shift/training scheduling
logistics, never a use-of-force/restraint/extraction operational
plan), a weapon/restraint-device-supply-forbidden check
(`:coordinate-supply-order` may never name a weapon or physical
restraint device, regardless of cost — this is a HARD block, not a
cost gate), and a content-based scope-exclusion check: any proposal
whose free text names a finalization/execution action for applying
physical restraint, using force, imposing a disciplinary sanction, or
restricting an incarcerated person's movement/confinement conditions
is a permanent HARD block, independent of and in addition to the
op-allowlist check. This actor **never** exercises, simulates
exercising, or proposes exercising any use-of-force, physical-restraint,
disciplinary-sanction, or movement/confinement-condition-restriction
authority — it only documents facility/equipment readiness and
coordinates officer logistics.

Always-escalate (human sign-off regardless of confidence, mapping this
repo's Trust Controls in
[`docs/business-model.md`](docs/business-model.md)):
`:flag-facility-concern` (surfacing an equipment/security-system/
staffing concern — always requires human corrections
officer/supervisor review; never auto-resolved, never in any phase's
auto-commit set — this is the ONLY channel by which a facility
observation may be surfaced) and any `:coordinate-supply-order` above
the registered cost threshold (a *non-weapon* item; a weapon/
restraint-device item is instead always a HARD block regardless of
cost).

## Robotics premise

All cloud-itonami verticals are designed on the premise that a **robot
performs the physical/administrative domain work**. Here a facility
documentation and logistics robot performs equipment/security-system
readiness data entry, staff shift/training scheduling and non-weapon
facility-equipment procurement coordination under an actor that
proposes actions and an independent **FacilityOpsGovernor** that gates
them. The governor never dispatches hardware itself;
`:high`/`:safety-critical` actions (such as flagging a facility
concern, or an above-threshold non-weapon supply order) require human
sign-off — and no action in this actor's closed op allowlist can ever
apply a physical restraint, use force, impose a disciplinary sanction,
or restrict an incarcerated person's movement or confinement
conditions.

## Core Contract

```text
officer intake queue + facility directory + supply policy
        |
        v
Facility Ops Advisor -> FacilityOpsGovernor -> log record/coordinate, or human sign-off
        |
        v
robot actions (gated) + operating records + audit ledger
```

No automated advice can dispatch a robot action the governor refuses,
apply a physical restraint, use force, impose a disciplinary sanction,
restrict an incarcerated person's movement or confinement conditions,
suppress an operating record, or disclose sensitive data without
governor approval and audit evidence.

## Capability layer

Resolves via [`kotoba-lang/occupation`](https://github.com/kotoba-lang/occupation)
(ISCO-08 `5413`). Required capabilities:

- :robotics
- :identity
- :audit-ledger

See [`docs/business-model.md`](docs/business-model.md) and
[`docs/operator-guide.md`](docs/operator-guide.md).

## License

AGPL-3.0-or-later.
