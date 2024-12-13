# User Api Specs

## Register User
- Endpoint : POST /api/users

Request Body: 
```json
{
  "username": "ruddi",
  "password": "112233",
  "name": "ruddi"
}
```
Response Body (Success):

```json
{
  "data": "OK"
}
```

Response Body (Failed):

```json
{
  "data": "KO",
  "errors": "Register Fail"
}
```

## Login User
- Endpoint : POST /api/auth/login

Request Body:
```json
{
  "username": "ruddi",
  "password": "112233"
}
```
Response Body (Success):

```json
{
  "data": {
    "token": "...token",
    "expiredAt": 24238 // expired milisecond
  }
}
```

Response Body (Failed , 401):

```json
{
  "errors": "Username or Password is wrong!"
}
```

## Get User
- Endpoint : GET /api/users/current

Request Header:

- X-API-TOKEN : TOKEN

Response Body (Success):

```json
{
  "data": {
    "username": "ruddi",
    "name": "ruddi"
  }
}
```

Response Body (Failed):

```json
{
  
  "errors": "Unauthorized"
}
```
## Update User
- Endpoint : PATCH /api/users

Request Header:

- X-API-TOKEN : TOKEN

Request Body:
```json
{
  "password": "112233",
  "name": "ruddi"
}
```
Response Body (Success):

```json
{
  "data": "OK"
}
```

Response Body (Failed):

```json
{
  "data": "KO",
  "errors": "Update Fail"
}
```

## Logout User
- Endpoint : DELETE /api/auth/logout

Request Header:

- X-API-TOKEN : TOKEN

  Response Body:
```json
{
  "data": "OK"
}
```