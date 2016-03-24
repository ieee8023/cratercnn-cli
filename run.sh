#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

java -cp `sh getclasspath.sh lib`:classes SlidingWindow $@

echo java finished
