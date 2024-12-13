# Address API Spec

## Create Address

Endpoint : POST /api/contacts/{idContacts}/addresses

Request Header :

- X-API-TOKEN : TOKEN

Request Body :

```json
{
  "street": "Jl. Contoh",
  "city": "Semarang",
  "province": "Jateng",
  "country": "Indonesia",
  "postalCode": "56352"
}
```

Response Body (Success) :

```json
{
  "id": "random string",
  "street": "Jl. Contoh",
  "city": "Semarang",
  "province": "Jateng",
  "country": "Indonesia",
  "postalCode": "56352"
}
```

Response Body (Failed) :

```json
{
  "error": "Contact is not found"
}
```

## Update Address

Endpoint :  PUT /api/contacts/{idContact}/adresses/{idAdress}

Request Header :

- X-API-TOKEN : TOKEN

Request Body :

```json
{
  "street": "Jl. Contoh",
  "city": "Semarang",
  "province": "Jateng",
  "country": "Indonesia",
  "postalCode": "56352"
}
```

Response Body (Success) :

```json
{
  "id": "random string",
  "street": "Jl. Contoh",
  "city": "Semarang",
  "province": "Jateng",
  "country": "Indonesia",
  "postalCode": "56352"
}
```

Response Body (Failed) :

```json
{
  "error": "Adress is not found"
}
```


## Get Address

Endpoint : GET /api/contacts/{idContact}/adresses/{idAdress}

Request Header :

- X-API-TOKEN : TOKEN


Response Body (Success) :

```json
{
  "id": "random string",
  "street": "Jl. Contoh",
  "city": "Semarang",
  "province": "Jateng",
  "country": "Indonesia",
  "postalCode": "56352"
}
```

Response Body (Failed) :

```json
{
  "error": "Adress is not found"
}
```

## Remove Address

Endpoint : DELETE /api/contacts/{idContact}/adresses/{idAdress}

Request Header :

- X-API-TOKEN : TOKEN


Response Body (Success) :

```json
{
  "data": "OK"
}
```

Response Body (Failed) :

```json
{
  "data": "Adress is not found"
}
```

## List Address

Endpoint : GET /api/contacts/{idContact}/addresses

Request Header :

- X-API-TOKEN : TOKEN


Response Body (Success) :

```json
{
  "data": [
    {
      "id": "random string",
      "street": "Jl. Contoh",
      "city": "Semarang",
      "province": "Jateng",
      "country": "Indonesia",
      "postalCode": "56352"
    }
  ]
}
```

Response Body (Failed) : 

```json
{
  "data": "Contact is not found"
}
```