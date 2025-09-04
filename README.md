 # Usage
1) Run on local machine
- mvn clean test (run tests)
- mvn allure serve (opens the generated Allure report in the default browser)

2) Execute via GitHub Action in the GitHub repository
  - download the report, unzip and for the unzipped folder run a http server
      - python -m http.server 8000
      - open the following URL in a browser: http://localhost:8000/index.html

  # NOTE
  Using the Spring Framework (or Spring Boot) is unnecessary in this specific task. Spring would only be needed to create own API that shall be tested (e.g., to develop the PetStore backend). 
  To test an existing, external API only a simple test project with RestAssured and Allure is enough.
