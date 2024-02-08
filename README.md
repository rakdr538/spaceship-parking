# spaceship-parking
space ship parking task as given by twoday

Stack:
* Spring Framework
* Spring boot
* MongoDB

Note:
* TODO: OAuth2.0
* Containerized testing


There is docker compose file to start mongoDB and mongoExpress

application.properties contains necessary monogodb and mongoExpress settings

Sample CURL:
curl -XPOST -H "Content-type: application/json" -d '{
    "parkingPlace": {
        "floor": 2,
        "plot": 14
    },
    "spaceShip": {
        "name": null,
        "registrationNumber": "@mine_own"
    },
    "spaceShipUser": {
        "user_id": "star-lord",
        "name": null
    }
}' 'http://localhost:8080/api/v1/parkings'
