
The integration tests are specified using Rspec.
These tests also need Selenium and Capybara, so Ruby (YARV) 2.1.2 and 
the following Gems are required:
 * rspec -v 3.1.0
 * capybara -v 2.4.4
 * selenium-webdriver -v 2.47.1
 
  

To run the specs:  
 * Edit the file `config.yaml` with valid Persona credentials.
 * Execute `rspec spec/features/user_signs_in_spec.rb`

