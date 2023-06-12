#!/bin/bash
set -eu -o pipefail

docker build --build-arg TARGET=$BUILD_TARGET -t $IMAGE_REPOSITORY:$IMAGE_TAG .
docker push $IMAGE_REPOSITORY:$IMAGE_TAG
