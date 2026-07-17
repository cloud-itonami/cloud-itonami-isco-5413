# Operator Guide

## First Deployment

1. Define the operator's facility scope and officer-assignment
   process.
2. Define consent and purpose categories for facility/equipment data
   handling.
3. Run synthetic operating scenarios (no real incarcerated-person,
   incident, officer or operator data in this repository).
4. Enable human-reviewed sign-off for `:high`/`:safety-critical`
   actions — including every `:flag-facility-concern` and every
   above-threshold `:coordinate-supply-order`.
5. Measure operating outcomes and audit coverage.

## Minimum Production Controls

- consent and disclosure log
- safety-critical escalation path
- provenance for all operating records
- human review for high-risk facility concerns
- audit export for all gated actions

## No Use-of-Force, Restraint, Disciplinary, or Confinement-Condition Authority

This actor is a facility/equipment/scheduling documentation/
administrative-logistics coordination robot ONLY. Operators MUST NOT
configure, extend or fork this actor to add an op that applies a
physical restraint, uses force, imposes a disciplinary sanction
(solitary confinement, privilege revocation, or any other sanction),
or restricts an incarcerated person's movement, access or confinement
conditions, or that otherwise exercises any custodial or enforcement
authority over an incarcerated person. Any such change removes the
structural guarantee this repository is built around and voids
certification (see [`GOVERNANCE.md`](../GOVERNANCE.md)). Every
facility observation must route through `:flag-facility-concern` to a
human corrections officer/supervisor — the robot's role ends at "here
is the readiness/concern data," never "here is what I recommend doing
about an incarcerated person."

Because incarcerated persons are a captive population with limited
ability to resist wrongful treatment, operators should treat any
proposed extension of this actor's scope toward custodial decisions as
categorically out of bounds, not a configuration option to weigh.

## Certification

Certified operators must prove that the governor gates every
safety-critical robot action, that safety-critical risks escalate to
humans, and that no build of this actor has ever added an op
resembling a use-of-force action, a physical restraint, a disciplinary
sanction, or a movement/confinement-condition restriction to the
closed allowlist.
