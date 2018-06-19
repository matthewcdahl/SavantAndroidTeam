# SavantAndroidTeam - Android APP

Made for the use of Savant's Android Team

Includes Features to help new team members as well as everyone in their sprint planning.

1. Getting Started: This features 6 icon buttons that link to the Savant Android Wiki
2. Poker: For the planning poker, users can create sessions and using Firebase upload their results. 
          Once the host has ended the session all results will be displayed.
3. Meetings: Allows users to create meetings and have them uploaded to Firebase for everyone
             on the app to see.
             
             
# Firebase

1. Structure

	I have the database structured into 3 sections. Poker, Meetings, and Users. 
	
	Poker - Every time someone creates a poker session it immediately gets uploaded to 
	        Firebase. It will assign it an id of whatever the previous sessions id is +1.
	        It will also log who the host is. When results start coming in there will be a subsection within
	        the session of responses which will log the users email as the key and the response as the value.
	        The nice thing about Firebase is that it is No SQL so I can have a varying number of responses each time and
	        that is not a problem.
                    
	Meetings - A meeting will not be pushed to Firebase until the host has entered all of the info for it and
	           clicked on submit. It will then populate with the name, time, place, description, date, and time.
		   
	Users - The first time a user logs in it will create a new section for them based on their email. When they make a nickname
	        and update their profile picture, that info will also be pushed into the database.

2. Challenges - The hardest thing was accessing the user information when I was dealing with the poker or meetings. I had to query all
	      of the data in the entire database to be able to access it. There is no specific query in Firebase that allows me to go 
	      across to the users section and grab info.
             


