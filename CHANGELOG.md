# Changelog


## [3.0.0](https://github.com/nyg/kraken-api-java/compare/v2.1.0..3.0.0) - 2025-11-29

### ⛰️  Features

- [`13accb9`](https://github.com/nyg/kraken-api-java/commit/13accb9329fb1984bb59c26bf2600ae48335cae9) **[breaking]** Search Assets endpoint result by pair name and altname ([#47](https://github.com/nyg/kraken-api-java/issues/47))

### 🐛 Bug Fixes

- [`9578cb4`](https://github.com/nyg/kraken-api-java/commit/9578cb44953fa3afd080b6e99fd79bc916bf0c4f) Improve staking reward filter and underlying asset detection ([#49](https://github.com/nyg/kraken-api-java/issues/49))
- [`6019526`](https://github.com/nyg/kraken-api-java/commit/6019526d635a60e6a6c1f76529629afe74dcf3a0) Avoid NPE if zip stream is empty in RetrieveExport endpoint ([#48](https://github.com/nyg/kraken-api-java/issues/48))
- [`04886c6`](https://github.com/nyg/kraken-api-java/commit/04886c65dabc690cc9a66880d92d39844220883c) Avoid sending null params for AssetPairs endpoint ([#46](https://github.com/nyg/kraken-api-java/issues/46))

### ⚙️ Miscellaneous

- [`b687d5f`](https://github.com/nyg/kraken-api-java/commit/b687d5fe97b2d8f56fd18eed8a052cb487d4a925) *(pom)* Sonatype requires name in library POM ([#52](https://github.com/nyg/kraken-api-java/issues/52))
- [`fa63aac`](https://github.com/nyg/kraken-api-java/commit/fa63aace0c355244f384a5fa6459eff6b5dc50c6) *(java)* **[breaking]** Update to Java 25 ([#51](https://github.com/nyg/kraken-api-java/issues/51))
- [`9feb33e`](https://github.com/nyg/kraken-api-java/commit/9feb33e7075f354e8745ce6654c5519c35e72808) *(examples)* Add yearly totals in staking rewards summary ([#50](https://github.com/nyg/kraken-api-java/issues/50))
- [`2caef2f`](https://github.com/nyg/kraken-api-java/commit/2caef2fe69cec6db2c968143742ceba7192d267e) Update log4j2 status level from info to warn ([#45](https://github.com/nyg/kraken-api-java/issues/45))
- [`c9c64e3`](https://github.com/nyg/kraken-api-java/commit/c9c64e3efd1887f91caca210243532b4927cb0ec) *(deps)* Update actions/checkout action to v6 ([#44](https://github.com/nyg/kraken-api-java/issues/44))
- [`fb2afbf`](https://github.com/nyg/kraken-api-java/commit/fb2afbf8a0eb6e8ea77185882058c78bf1355471) *(deps)* Update dependency org.codehaus.mojo:exec-maven-plugin to v3.6.2 ([#43](https://github.com/nyg/kraken-api-java/issues/43))
- [`2a2455b`](https://github.com/nyg/kraken-api-java/commit/2a2455b18ebc400e01f2b73b0bc302edade8b2af) *(deps)* Update all stable non-major dependencies ([#42](https://github.com/nyg/kraken-api-java/issues/42))
- [`b9b46e5`](https://github.com/nyg/kraken-api-java/commit/b9b46e518c185cd191366011fb9c03c83b727b76) *(deps)* Update actions/setup-java action to v5 ([#41](https://github.com/nyg/kraken-api-java/issues/41))
- [`9ab82f4`](https://github.com/nyg/kraken-api-java/commit/9ab82f43f0bb486122d13f4b242396f056f5f4e1) *(deps)* Update actions/checkout action to v5 ([#40](https://github.com/nyg/kraken-api-java/issues/40))
- [`459df18`](https://github.com/nyg/kraken-api-java/commit/459df183f4bfdce6e8ebc4463342a163a8b4b353) *(deps)* Update all stable non-major dependencies ([#39](https://github.com/nyg/kraken-api-java/issues/39))
- [`bf93d4c`](https://github.com/nyg/kraken-api-java/commit/bf93d4c25583edc25ee4840a5b0dad5b24979fdc) *(deps)* Update all stable non-major dependencies ([#38](https://github.com/nyg/kraken-api-java/issues/38))
- [`c70d85a`](https://github.com/nyg/kraken-api-java/commit/c70d85a5b3e6188b8183d09333780544caa88888) *(deps)* Update all stable non-major dependencies ([#37](https://github.com/nyg/kraken-api-java/issues/37))
- [`44cfa69`](https://github.com/nyg/kraken-api-java/commit/44cfa6915d43a65c47bf89e9cf5bf51447c55ced) *(deps)* Update all stable non-major dependencies ([#36](https://github.com/nyg/kraken-api-java/issues/36))
- [`c3daf7f`](https://github.com/nyg/kraken-api-java/commit/c3daf7f266e057f1e74486d4e3f70a74263f42bf) *(deps)* Update dependency com.fasterxml.jackson:jackson-bom to v2.18.3 ([#35](https://github.com/nyg/kraken-api-java/issues/35))
- [`8340cca`](https://github.com/nyg/kraken-api-java/commit/8340cca13b5b5de4345217a868b0e891676f50d8) *(deps)* Update all stable non-major dependencies ([#34](https://github.com/nyg/kraken-api-java/issues/34))
- [`95a3e75`](https://github.com/nyg/kraken-api-java/commit/95a3e756b4c588cb299f4d0a0c326301db1f6f7e) *(deps)* Update all stable non-major dependencies ([#32](https://github.com/nyg/kraken-api-java/issues/32))
- [`335d136`](https://github.com/nyg/kraken-api-java/commit/335d136245c09d1b5cf880df8f2e49a08535f01e) *(ci)* Add GitHub Actions workflow for building PRs ([#33](https://github.com/nyg/kraken-api-java/issues/33))
- [`f942b94`](https://github.com/nyg/kraken-api-java/commit/f942b94dee45b64b738c09908aa5d626648e3296) *(readme)* Improve README ([#29](https://github.com/nyg/kraken-api-java/issues/29))
- [`6fc5676`](https://github.com/nyg/kraken-api-java/commit/6fc567686148dcb602b1bdfe716c8a67268a4728) *(examples)* Faster reward summary generation ([#30](https://github.com/nyg/kraken-api-java/issues/30))
- [`3b5bb1b`](https://github.com/nyg/kraken-api-java/commit/3b5bb1ba6cd8b8a5db41f97c9a8fb04d83668438) *(deps)* Auto-merge Renovate PRs every month ([#28](https://github.com/nyg/kraken-api-java/issues/28))

## [2.1.0](https://github.com/nyg/kraken-api-java/compare/v2.0.0..v2.1.0) - 2024-12-26

### ⛰️  Features

- [`934b794`](https://github.com/nyg/kraken-api-java/commit/934b7945b1121d2e582195d4c3d5dbd2d0466532) Implement report endpoints, add EOY Balance example ([#24](https://github.com/nyg/kraken-api-java/issues/24))

### ⚙️ Miscellaneous

- [`66aabb5`](https://github.com/nyg/kraken-api-java/commit/66aabb53cfa3faf11a5f5b03dc8531b6e81dd320) *(readme)* Update README for v2.1.0
- [`bb94609`](https://github.com/nyg/kraken-api-java/commit/bb946092329c4661f03ea960e31e2f90b76a7dcf) *(deps)* Update all non-major dependencies ([#20](https://github.com/nyg/kraken-api-java/issues/20))
- [`0cee177`](https://github.com/nyg/kraken-api-java/commit/0cee1772c125f43bc9c876f15fd3e5f13f04fd9d) *(java)* Explicitly specify annotation processors ([#26](https://github.com/nyg/kraken-api-java/issues/26))
- [`8aaa739`](https://github.com/nyg/kraken-api-java/commit/8aaa73946f9167f952313633593b2d516a391028) *(deps)* Update orhun/git-cliff-action action to v4 ([#22](https://github.com/nyg/kraken-api-java/issues/22))
- [`274ddb2`](https://github.com/nyg/kraken-api-java/commit/274ddb2f3c6595ec1a818d0bbc3c003f68553170) *(doc)* Document the release process ([#19](https://github.com/nyg/kraken-api-java/issues/19))
- [`b9e7684`](https://github.com/nyg/kraken-api-java/commit/b9e7684968a78e0701c70e727a810776b90032f0) *(readme)* Update README for v2

## [2.0.0](https://github.com/nyg/kraken-api-java/compare/v1.0.0..v2.0.0) - 2024-03-29

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
