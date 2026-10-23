# Building and sideloading BitChord for iPhone

The [Build iOS IPA](.github/workflows/build-ios-ipa.yml) GitHub Actions workflow builds the
SwiftUI iOS app, Kotlin Multiplatform framework, and Rust audio engine on an
Apple Silicon Xcode runner.

## Obtain the IPA

1. Open [GitHub Actions → Build iOS IPA](https://github.com/dn4hc/BitChord/actions/workflows/build-ios-ipa.yml).
2. Select **Run workflow** on `main` if there is not already a successful run.
3. Open a completed run with a green checkmark.
4. Download the **BitChord-iOS-unsigned** artifact from the run's **Artifacts** section.
5. Extract the downloaded ZIP to obtain **BitChord-unsigned.ipa**.

The IPA is an unsigned *device* build (arm64, iOS 18 or later), not an
App Store package. GitHub's artifact ZIP is not itself the IPA.

## Signing and installing

Use your preferred iOS sideloading or signing workflow (such as AltStore,
SideStore, or an equivalent). The signing tool must sign the main application,
embedded frameworks, and the widget extension using an appropriate provisioning
profile, and adjust or provision the bundle identifiers/App Group entitlement as
required by your account.

An unsigned IPA will not install directly by tapping it in Files. The widget
and shared App Group may require additional signing capabilities. A successful
CI compile alone does not guarantee the sideload will install or all features
will work.

## Source add-ons

Configure any third-party add-on inside the running app rather than embedding
unreviewed add-on URLs into the build. Verify the provider and its HTTPS endpoint
before adding it, and never give an unknown provider account credentials or
authentication tokens.

## Building locally

If the CI build needs debugging on your M-series Mac, follow the full
source-build instructions in [README.md](README.md). The Xcode project and
native frameworks are generated and are not committed to the repository.
