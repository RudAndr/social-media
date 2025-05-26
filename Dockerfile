FROM ubuntu:latest
LABEL authors="rudor"

ENTRYPOINT ["top", "-b"]