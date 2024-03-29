# Changelog


## [2.0.0](https://github.com/nyg/kraken-api-java/compare/v1.0.0..2.0.0) - 2024-03-29

### ⛰️  Features

- [`7bc46b8`](https://github.com/nyg/kraken-api-java/commit/7bc46b8655338d0b72608802fd5ab0c9c9f2a699) **[breaking]** Ability to specify custom nonce generator ([#16](https://github.com/nyg/kraken-api-java/issues/16))
- [`34915c4`](https://github.com/nyg/kraken-api-java/commit/34915c460ba4e83c8c71f1e79cbc1ae62dea7153) **[breaking]** Implement Ticker, Ledgers and QueryLedgers endpoints ([#12](https://github.com/nyg/kraken-api-java/issues/12))
- [`2d7e766`](https://github.com/nyg/kraken-api-java/commit/2d7e766105684ad9683deba990b33aff95881659) Add SLF4J library ([#11](https://github.com/nyg/kraken-api-java/issues/11))
- [`47b5d94`](https://github.com/nyg/kraken-api-java/commit/47b5d94987738739e5b07279d62532708ab3e945) **[breaking]** Return JsonNode instead of String for API calls ([#3](https://github.com/nyg/kraken-api-java/issues/3))

### 🚜 Refactor

- [`b047f47`](https://github.com/nyg/kraken-api-java/commit/b047f47fc158fcd985630fdd9dac6f10ba5c4dcf) **[breaking]** Adopt a more object-oriented approach ([#8](https://github.com/nyg/kraken-api-java/issues/8))

### ⚙️ Miscellaneous

- [`8028610`](https://github.com/nyg/kraken-api-java/commit/8028610b1b850261f8a32170f909604353e139b2) *(publish)* Update central-publishing plugin config
- [`6a81fd4`](https://github.com/nyg/kraken-api-java/commit/6a81fd4878f4ee679cb45f8bcf4c6eab87a539d4) *(deps)* Update all non-major dependencies ([#6](https://github.com/nyg/kraken-api-java/issues/6))
- [`ab3c048`](https://github.com/nyg/kraken-api-java/commit/ab3c048e993af7e9ec413f2512ba1f1661e418f4) *(examples)* Usage of nested POST parameters ([#15](https://github.com/nyg/kraken-api-java/issues/15))
- [`ed6487a`](https://github.com/nyg/kraken-api-java/commit/ed6487a6bbf236b1d50cd698def66e8da5ca6c9e) *(java)* **[breaking]** Update to Java 21 ([#7](https://github.com/nyg/kraken-api-java/issues/7))
- [`0d9b0da`](https://github.com/nyg/kraken-api-java/commit/0d9b0dae0d676fe58521477f7cf817606ae742e0) *(deps)* Update dependency org.codehaus.mojo:exec-maven-plugin to v3.2.0 ([#2](https://github.com/nyg/kraken-api-java/issues/2))
- [`abd3026`](https://github.com/nyg/kraken-api-java/commit/abd302605e77e2328eb16f9299f5d3cd762aa064) *(deps)* Update orhun/git-cliff-action action to v3 ([#5](https://github.com/nyg/kraken-api-java/issues/5))
- [`698c584`](https://github.com/nyg/kraken-api-java/commit/698c58452514499a439d3c5c240aeffda1fe06e6) *(deps)* Add Renovate configuration file
- [`5cc538c`](https://github.com/nyg/kraken-api-java/commit/5cc538c9143b2577f8a0967fc434fc9835bda863) *(readme)* Add link to Sonatype on maven-central badge

## 1.0.0 - 2024-01-30

### Others

- [`1a46fd6`](https://github.com/nyg/kraken-api-java/commit/1a46fd65cccc3a1c626b62402046257b12d057c9) Update release and changelog scripts
- [`f757236`](https://github.com/nyg/kraken-api-java/commit/f757236d39bbbbff734251cb82a8e126875f9ca1) Revert to Java 8
- [`34050d6`](https://github.com/nyg/kraken-api-java/commit/34050d6db4b231704215d7944a93ff2c9658a754) Update README instructions
- [`65114c3`](https://github.com/nyg/kraken-api-java/commit/65114c363638d722aff370289c486ca068c324a6) Change to version 1.0.0-SNAPSHOT
- [`951a4dc`](https://github.com/nyg/kraken-api-java/commit/951a4dcab276d841897dabfbd4312c04ab5363cf) Rename Java package
- [`3013703`](https://github.com/nyg/kraken-api-java/commit/30137035e5520015a43967c1f1985b9f0dd95161) Create GitHub release when tag is pushed
- [`d9de827`](https://github.com/nyg/kraken-api-java/commit/d9de827903d60af469433a05dad952af5d8f5212) Configure maven-release-plugin and git-cliff
- [`e9d02be`](https://github.com/nyg/kraken-api-java/commit/e9d02beb8aec494810c6935db4d3470e4079057b) Fix Lombok issue with constructor
- [`f6d8884`](https://github.com/nyg/kraken-api-java/commit/f6d8884607ce8039d9100fe26431ebcf430631ef) Refactor into separate Maven modules
- [`3d79434`](https://github.com/nyg/kraken-api-java/commit/3d79434e8c2c15ae516e434dcb89e44161f97c2a) Minor formatting changes
- [`1639dd8`](https://github.com/nyg/kraken-api-java/commit/1639dd8ad46dfa0a07ccd72dc7fce8be1b8c010f) Simplify POM file
- [`d8a160c`](https://github.com/nyg/kraken-api-java/commit/d8a160c7bd028c4601d8801b2cd4a6aaedd53c62) Add .gitignore
- [`8d827ae`](https://github.com/nyg/kraken-api-java/commit/8d827ae515bbdd4093528e2a438f08551c77defe) Added instructions to build and run
- [`6fbe590`](https://github.com/nyg/kraken-api-java/commit/6fbe5904160cadfcace79ffff45368f4ac3025e2) Removed unused dependencies
- [`d456292`](https://github.com/nyg/kraken-api-java/commit/d45629257372d79645027f05e1d2b527552727c8) Now using Maven
- [`f2573c0`](https://github.com/nyg/kraken-api-java/commit/f2573c0d2e3237b2a203ca524259d85c6239eadc) Update README.md
- [`9e616b8`](https://github.com/nyg/kraken-api-java/commit/9e616b87bbd99f733934b5a273f65b8c78eca8ca) Commit project
- [`4aac798`](https://github.com/nyg/kraken-api-java/commit/4aac7988b04a82227d113fd3a8a7e60645e34c96) Initial commit

<!-- generated by git-cliff -->
