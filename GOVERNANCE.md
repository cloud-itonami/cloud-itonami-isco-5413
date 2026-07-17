# Governance

`cloud-itonami-isco-5413` is an OSS open-occupation blueprint. Governance covers
both code and the operator model.

## Maintainers

Maintainers may merge changes that preserve these invariants:

- the Advisor cannot directly dispatch robot actions or disclose records.
- FacilityOps Governor remains independent of the advisor.
- hard policy violations cannot be overridden by human approval.
- the closed proposal-op allowlist NEVER gains an op that applies a
  physical restraint, uses force, imposes a disciplinary sanction
  (solitary confinement, privilege revocation, etc.), or restricts an
  incarcerated person's movement, access or confinement conditions, or
  that otherwise exercises any custodial or enforcement authority over
  an incarcerated person — this is a permanent scope boundary of the
  project, not subject to normal maintainer discretion.
- every commit, hold and approval path is auditable.
- real incarcerated-person, incident, officer or operator data stays
  outside Git.

## Decision Records

Architecture decisions live in `docs/adr/`. Changes to the trust model,
storage contract, public business model, operator certification or license
should add or update an ADR.

## Operator Governance

Anyone may fork and operate independently. itonami.cloud certification is a
separate trust mark and should require security, audit, support and data-flow
review.

Certified operators can lose certification for:

- bypassing policy checks
- mishandling incarcerated-person, incident, officer or operator data
- misrepresenting certification status
- failing to respond to security incidents
- hiding material changes to customer-facing operation
- adding, or attempting to add, any capability that applies a physical
  restraint, uses force, imposes a disciplinary sanction, or restricts
  an incarcerated person's movement, access or confinement conditions
