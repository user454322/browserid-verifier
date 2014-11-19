require 'capybara/rspec'
require 'yaml'

describe "sign in", :type => :feature, :js => true do
  before(:all) do
    puts "Nanoni"
    config = YAML.load_file('config.yaml')
    @email = config['authentication_email']
    @passwd = config['authentication_password']
  end

  it "signs in the user using Mozilla's Persona" do
    visit 'https://browseridverifiersample-user454322.rhcloud.com'
    persona_window = window_opened_by do
      click_button 'persona_sign_in_up'
    end
    within_window persona_window do
      Capybara.default_wait_time = 10
      find_by_id('authentication_email').set(@email)
      find(:xpath, '//*[@id="authentication_form"]/p[4]/button[1]').click
      find_by_id('authentication_password').set(@passwd)
      find(:xpath, '//*[@id="authentication_form"]/p[4]/button[3]').click
    end
  end
end
#https://github.com/jnicklas/capybara

