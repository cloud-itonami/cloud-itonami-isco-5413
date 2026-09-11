# Contributing

`cloud-itonami-isco-5413` accepts contributions to the OSS actor, policy tests,
documentation, examples and open occupation blueprint.

## Development

```bash
kbb -M:dev:test
kbb -M:lint
```

Keep changes small and include tests for policy, audit, store or disclosure
behavior.

## Rules

- Do not commit real incarcerated-person, incident, officer or
  operator data, credentials or operating documents.
- Keep production writes and disclosures behind FacilityOps Governor.
- **Never add an op that applies a physical restraint, uses force,
  imposes a disciplinary sanction (solitary confinement, privilege
  revocation, etc.), or restricts an incarcerated person's movement,
  access or confinement conditions, or that otherwise exercises any
  custodial or enforcement authority over an incarcerated person.**
  This actor's closed proposal-op allowlist is a hard scope boundary,
  not a starting point to extend. Any PR that proposes such an op will
  be rejected.
- Treat this occupation's workflows as high-risk: add tests for permission,
  purpose, safety and audit logging.
- Document any new business-model or operator assumption in `docs/`.

## Pull Requests

PRs should describe:

- what behavior changed
- which policy invariant is affected
- how it was tested
- whether operator or certification docs need updates
