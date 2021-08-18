test:
	docker run --rm -it -v `pwd`:`pwd` -w `pwd` swift swift test

coverage:
	docker run --rm -it -v `pwd`:`pwd` -w `pwd` swift \
		swift test --enable-code-coverage && echo "\n" && \
		llvm-cov report .build/x86_64-unknown-linux-gnu/debug/WikimediaMetricsPlatformPackageTests.xctest \
		-instr-profile=.build/x86_64-unknown-linux-gnu/debug/codecov/default.profdata \
		-ignore-filename-regex=".build|Tests" \
		-use-color
