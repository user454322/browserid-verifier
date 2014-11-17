require 'capybara/rspec'

describe "sign in", :type => :feature, :js => true do

  it "signs the user in" do
    visit 'https://browseridverifiersample-user454322.rhcloud.com'
    click_button 'persona_sign_in_up'
  end

end


