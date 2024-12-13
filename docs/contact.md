# Contact API Spec

## Create Contact

Endpoint : POST /api/contacts

Request Header : 

- X-API-TOKEN : TOKEN

Request Body: 

```json
{
  "firstName": "ruddi",
  "lastName": "khai",
  "email": "ruddi@mail.com",
  "phone": "123456789"
}
```

Response Body (Success):

```json
{
  "data": {
    "id": "random string",
    "firstName": "ruddi",
    "lastName": "khai",
    "email": "ruddi@mail.com",
    "phone": "123456789"
  }
}
```

Response Body (Failed):

```json
{
  "errors": "Failed to Create Contact"
}
```

## Update Contact

Endpoint : PUT /api/contacts/{idContact}

Request Header :

- X-API-TOKEN : TOKEN

Request Body: 

```json
{
  "firstName": "ruddi",
  "lastName": "khai",
  "email": "ruddi@mail.com",
  "phone": "123456789"
}
```


Response Body (Success):

```json
{
  "id": "random string",
  "firstName": "ruddi",
  "lastName": "khai",
  "email": "ruddi@mail.com",
  "phone": "123456789"
}
```

Response Body (Failed):

```json
{
  "errors": "Failed to Create Contact"
}
```

## Get Contact

Endpoint : GET /api/contacts/{idContanct}

Request Header :

- X-API-TOKEN : TOKEN


Response Body (Success):

```json
{
  "data": {
    "id": "random string",
    "firstName": "ruddi",
    "lastName": "khai",
    "email": "ruddi@mail.com",
    "phone": "123456789"
  }
}
```

Response Body (Failed,404):

```json
{
  "errors": "Failed to Get Contact"
}
```

## Search Contact

Endpoint : GET /api/contacts

Query Param :

// Optional

- name : String, contact first or last name, using like query
- phone : String, contact phone, using like query
- email : String, contact email, using like query
- page : Integer, start from 0
- size : Integer, default 10

Request Header :

- X-API-TOKEN : TOKEN


Response Body (Success):

```json
{
  "data": [
    {
    "id": "random string",
    "firstName": "ruddi",
    "lastName": "khai",
    "email": "ruddi@mail.com",
    "phone": "123456789"
    }
  ],
  "paging": {
    "currentPage":0,
    "totalPage": 10,
    "size": 10
  }
}
```

Response Body (Failed):
```json
{
  "errors": "Unauthorize"
}
```

## Remove Contact

Endpoint : DELETE   /api/contacs/{idContacts}

Request Header :

- X-API-TOKEN : TOKEN


Response Body (Success):

```json
{
  "data": "OK"
}
```

Response Body (Failed):

```json
{
  "errors": "Failed to Delete Contact"
}
```