# YNAB Reconcile

Tool to compare [YNAB](https://ynab.com) transactions to transactions from a bank. Transaction lists have to be provided
via CSV files. Bank CSV format is currently the one exported from the [DKB online banking](https://dkb.de)
(**you'll need to remove the first couple of lines first**).

```
Usage: <main class> [-dhV] <ynabCsv> <bankCsv>
      <ynabCsv>   The CSV file exported from YNAB (c.f. https://support.ynab.
                    com/en_us/how-to-export-budget-data-Sy_CouWA9)
      <bankCsv>   The CSV file exported from your bank
  -d, --debug
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
```

Example output (for CSV files in `src/test/resources`):

```
Transactions only found in Bank
22.06.2023, Some Payee 2 slightly different, Some Notes 2, -13.37€

Transactions only found in YNAB
24.06.2023, Some Payee 2, Some Category 2:Some Memo 2, 69.00€

Matching transactions (YNAB <----> Bank)
22.06.2023, Some Payee 1, Some Category 1:Some Memo 1, -4.20€ <----> 20.06.2023, Some Payee 1 slightly different, Some Notes 1, -4.20€
```

## Development Setup

- Install [GraalVM](https://www.graalvm.org/) (e.g. via IntelliJ) and make it the default JVM on your system (set
  JAVA_HOME, jenv, ...).
- Import project into IntelliJ. There are several run configs available for building, running and testing.
- In order for reflection to work, GraalVM needs some configuration which is done via the files
  in `src/main/resources/META-INF/native-image`. They can be (re-) generated using the IntellJ run
  config `GraalVM Collect Metadata`. Documentation how that works can be found in
  the [GraalVM documentation](https://www.graalvm.org/22.0/reference-manual/native-image/Agent/).