@txn
Feature: Sign up
  New user try to sign up

  Scenario: User sign up success
	Given I am not logged in yet
	When I am on login page
	Then I should see sign up link
	When I click sign up link
	Then I should see sign up button
	And I fill in "userName" with "my-user-name"
	And I fill in "password1" with "my-pw"
	And I fill in "password2" with "my-pw"
	And I click sign up button
	Then I redirected to my notes page
	And I should see "my-user-name's notes" text

  Scenario: User sign up failed
	Given I am not logged in yet
	When I am on login page
	Then I should see sign up link
	When I click sign up link
	Then I should see sign up button
	And I fill in "userName" with "my-user-name"
	And I fill in "password1" with "my-pw1"
	And I fill in "password2" with "my-pw2"
	And I click sign up button
	Then I should see error message "Password does not match the confirm password."