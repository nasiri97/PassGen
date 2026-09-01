# Testing Policy

This project uses automated testing to ensure code quality and prevent regressions.

## Automated CI/CD

We have a GitHub Actions workflow configured in `.github/workflows/ci.yml` that automatically runs all tests whenever code is:
- Pushed to the `master` or `main` branch.
- Submitted via a Pull Request to the `master` or `main` branch.

## Manual Testing

Before pushing your changes, please run the tests locally to ensure everything is working as expected.

### Run All Tests
```bash
./gradlew check
```

### Module Specific Tests
To run tests for the `codec` module:
```bash
./gradlew :codec:jvmTest
```

To run tests for the `shared` module (JVM):
```bash
./gradlew :shared:jvmTest
```

## Writing Tests
- All new features in the `codec` module must include unit tests in `src/commonTest`.
- Ensure that edge cases (like empty inputs or invalid sizes) are covered.
