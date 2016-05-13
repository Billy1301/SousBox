Questions to answer:

1. Does the project appear to meet the technical requirements? Write up one sentence on your findings and give a score 0-3.
  stew: the only thing keeping you from a 3 is adding another thread for database interactions or api calls. nice use of fragments

Is your peer making API calls, using SDK's/third-party libraries?
  stew: yes, multiple api calls using retrofit.
  
Is your peer making use of Services? If so, are they offloading long tasks to a separate thread, i.e. AsyncTask, Runnable, IntentService, etc.
  stew: no clear use of services, a notification might be a good way of using a service.
  
Is your peer making use of Fragments? If so, are they passing data from Fragment to Activity via interfaces? If not, why did absense of Fragments make sense?
  stew: really nice use of fragments. looks like youre passing data from the fragment to the activity, good job.
  
Is your peer making use of RecyclerView? If so, does it appear to be working correctly ( implementation and otherwise )?
  stew: yes, looks and works great
  
Is your peer making use of some sort of persistent storage, i.e. Firebase or SQLite? If so, why do you think Firebase/SQLite was chosen? Could they have used one or the other instead and why?
  stew: yes, the app makes use of a firebase db for user preferences which is appropriate. e.g. the user might want to use the app on multiple devices.
  
2. Does the project appear to be creative, innovative, and different from any competition? Write up one sentence on your findings and give a score 0-3.
  stew: 3 the app builds on other recipe apps, making a more social and interactive recipe app to use with friends

Is your peer making use of proper UX patterns we learned in class? If not, what are they doing that is unconvetional or that might confuse a user ( you )?
  stew: the user flow is very clear and everything is very conventional. no surprises at all, very intuitive.

Is your peer making anything cool or awesome that you would like to note or applaud them on?
  stew: the tap and drag/ swipe functionality is really cool. awesome job.
  
3. Does the project appear to follow correct coding styles and best practices? Write up one sentence on your findings and give a score 0-3.
 stew: 3, yes very easy to follow your code. 

Are you able to reasonably follow the code without having anyone answer your questions?
  yes
Are you able to make sense of what the code is doing or is trying to do?
  yes
  
4. Find two pieces of code of any size: one that is readable and easy to follow and one that is difficult to follow and understand.

  difficult to read code:
  
  private void retrofitRecipe() {
       // String getSearch = queryFilters.getQuery();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        searchAPI = retrofit.create(RecipeAPI.class);

        Call<SpoonacularResults> call = searchAPI.searchRecipe(querySearch);
        call.enqueue(new Callback<SpoonacularResults>() {
            @Override
            public void onResponse(Call<SpoonacularResults> call, Response<SpoonacularResults> response) {
                SpoonacularResults spoonacularResults = response.body();

                if(spoonacularResults == null){
                    return;
                }

                Collections.addAll(recipeLists, spoonacularResults.getResults());

                if (recyclerView != null) {
                    long seed = System.nanoTime();
                    Collections.shuffle(recipeLists, new Random(seed));

                    recyclerView.setAdapter(recycleAdapter);
                    recycleAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<SpoonacularResults> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

  stew: i think i know what this code is doing, but a clarifying comment above the method would make it extra easy to read. I'm sure you were planning on doing this before Friday.

  easy to read code:
  
  private void bottomNavi(){
        AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

        // Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.tab_1, R.drawable.ic_menu_gallery, R.color.colorPrimary);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.tab_2, R.drawable.ic_menu_share, R.color.colorPrimaryDark);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.tab_3, R.drawable.ic_menu_manage, R.color.colorAccent);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem(R.string.tab_4, R.drawable.ic_menu_manage, R.color.colorPrimary);

        // Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.addItem(item4);

        // Set background color
        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FEFEFE"));

        // Disable the translation inside the CoordinatorLayout
        bottomNavigation.setBehaviorTranslationEnabled(false);

        // Change colors
        bottomNavigation.setAccentColor(Color.parseColor("#F63D2B"));
        bottomNavigation.setInactiveColor(Color.parseColor("#747474"));

        // Force to tint the drawable (useful for font with icon for example)
        bottomNavigation.setForceTint(true);

        // Force the titles to be displayed (against Material Design guidelines!)
        bottomNavigation.setForceTitlesDisplay(true);

        // Use colored navigation with circle reveal effect
        bottomNavigation.setColored(true);

        // Set current item programmatically
        bottomNavigation.setCurrentItem(2);
  
  stew: the above code is easy to read despite its length. The comments throughout help the flow. I can read it without even needing to pause to comprehend what's going on.

What makes the readable code readable? Be as detailed as you can in your answer, it can be challenging to explain why something is easy to undertand
What makes the difficult code harder to follow? Be as detailed as you can in your answer.
5. High level project overview: Take a look at as many individual files as you have time for

Does this class make sense?
Does the structure of the class make sense?
Is it clear what this class is supposed to do?
Status API Training Shop Blog About
© 2016 GitHub, Inc. Terms Privacy Security Contact Help
