# WebdevProjectApi

CS4550 2018 Summer 1 final project by Jingyu Yao.

- [Live site](http://jingyuyao-webdev-project.herokuapp.com/)
- [Client repo](https://github.com/jingyuyao/webdev-project)
- [Project requirements](https://docs.google.com/document/d/1De-UdZ8LpJt6tftlCsYcZz-BCyh8Nljz7KYO5DY00_8/edit?usp=sharing).

## Local development

Booting up a local server:
```
DBHOSTNAME=$hostname$ DBSCHEMA=$schema$ DBUSERNAME=$username$ DBPASSWORD=$password$ \
  MASHAPE_KEY=$mashapekey$ \
  ./mvnw spring-boot:run
```
where the environment variables point to a MySql database.
