FROM mongo:6.0

COPY ./mongo-configuration/keyfile /data/keyfile
RUN chmod 400 /data/keyfile && chown 999:999 /data/keyfile

COPY ./mongo-configuration/mongod.conf /etc/mongod.conf

CMD ["mongod", "--config", "/etc/mongod.conf", "--auth"]
