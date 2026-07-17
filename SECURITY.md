# Security Policy

This project handles prison guards operating workflows, directly
touching the bodily safety, liberty and conditions of confinement of a
captive population with limited ability to resist wrongful treatment.
Treat vulnerabilities as potentially high impact even when the demo
data is synthetic.

## Do Not Disclose Publicly

Report privately before opening public issues for:

- credential exposure
- real incarcerated-person, incident, officer or operator data
  exposure
- authorization bypass
- FacilityOps Governor bypass
- any path by which the actor could apply a physical restraint, use
  force, impose a disciplinary sanction, or restrict an incarcerated
  person's movement, access or confinement conditions
- audit-ledger tampering
- over-disclosure in reports or exports
- unsafe robot action dispatch

## Reporting

Use GitHub private vulnerability reporting when available for the repository.
If that is unavailable, contact the repository maintainers through the
cloud-itonami organization before publishing details.

Include:

- affected commit or version
- reproduction steps
- expected and actual behavior
- impact on facility data, policy enforcement or audit logging
- suggested fix, if known

## Production Guidance

- Store secrets outside Git.
- Keep real incarcerated-person/incident/officer/operator data outside this repository.
- Run policy tests before deployment.
- Export and review audit logs regularly.
- Use least privilege for operators and service accounts.
