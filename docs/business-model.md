# Business Model: Correctional Facility Equipment/Scheduling Documentation and Administrative-Logistics Coordination Practice

## Classification

- Repository: `cloud-itonami-isco-5413`
- ISCO-08: `5413`
- Occupation: Prison Guards
- Social impact: custodial-safety-integrity, due-process-integrity, facility-readiness

## Customer

- correctional facilities / departments of corrections
- individual corrections officers and shift supervisors (direct users
  of the coordination tooling)

## Offer

- equipment / security-system readiness metadata entry
- staff shift/training operation scheduling coordination
- facility-concern flagging (surfacing equipment/security-system/
  staffing concerns for human corrections officer/supervisor review)
- non-weapon facility-equipment procurement coordination

## Revenue

- monthly facility/department retainer
- per-facility documentation-coordination fee

## Trust Controls

- **no use-of-force, physical-restraint, disciplinary-sanction, or
  movement/confinement-condition-restriction authority exists in this
  actor.** The closed proposal-op allowlist never includes an op that
  could apply a physical restraint, use force, impose a disciplinary
  sanction (solitary confinement, privilege revocation, etc.), or
  restrict an incarcerated person's movement, access or confinement
  conditions — such capabilities are structurally absent, not merely
  gated.
- no proposal commits or escalates without an independently registered
  AND verified officer record (and, for facility-referencing ops, an
  independently registered AND verified facility record matching the
  officer's own assignment)
- equipment/security-system readiness log entries are physical
  condition metadata only, never an identification of or conclusion
  about a specific incarcerated person
- staff shift/training scheduling never records a use-of-force,
  restraint or cell-extraction operational plan
- non-weapon facility-equipment supply orders can never name a weapon
  or physical-restraint device, regardless of cost — this is an
  unconditional hard block, not a cost-based gate
- `:flag-facility-concern` always requires human corrections
  officer/supervisor sign-off, never auto-resolved — this is the only
  channel by which a facility observation may be surfaced
- non-weapon facility-equipment supply orders above the registered
  cost threshold always require human sign-off
- facility documentation and logistics-coordination records are
  auditable, not editable
