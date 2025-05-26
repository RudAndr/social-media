rs.initiate({
    _id: 'rs0',
    members: [
        { _id: 0, host: "mongo_primary:27017" },
        { _id: 1, host: "mongo_secondary1:27017" },
    ]
});

print("Replica set initiated!");

printjson(rs.status());
