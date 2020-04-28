using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using TMPro;
using Firebase;
using Firebase.Database;
using Firebase.Unity.Editor;


public class DBStartup : MonoBehaviour
{
    
    // reference to root directory on the firebase database
    public DatabaseReference root;
    // reference to dynamic message on the debug panel
    public GameObject debugTextObj;

    // Start is called before the first frame update
    void Start()
    {
        // Connect to Firebase, start panel update threads and game loop
        FirebaseApp.CheckAndFixDependenciesAsync().ContinueWith(task => {
            var dependencyStatus = task.Result;
            if (dependencyStatus == DependencyStatus.Available)
            {
                FirebaseApp.DefaultInstance.SetEditorDatabaseUrl("https://wolfgame-22e75.firebaseio.com/");
                root = FirebaseDatabase.GetInstance(FirebaseApp.DefaultInstance).RootReference;
                Debug.Log("Database root connected at: " + root);
                mainPanel.GetComponent<PopupAnimate>().AnimOpen();
                // debug panel update
                CountUpdate();
                GamestateUpdate();
                ReadyCountUpdate();
                // game panel updates
                StartCoroutine(LobbyUpdate());
            }
            else
            {
                Debug.LogError(string.Format(
                  "Could not resolve all Firebase dependencies: {0}", dependencyStatus));
                // Firebase Unity SDK is not safe to use here.
            }
        });
    }

    /*=============================================================================================
     *  AUXILLARY callable functions
     *============================================================================================*/
    void ExitClient()
    {
        ResetLobby();
        Application.Quit();
    }

    void ResetLobby()
    { //For now, we must restart the game if we reset lobby to play again
        StopAllCoroutines();
        debugTextObj.GetComponent<PanelTextControl>().SetNewText(
            "[Lobby Reset]\n" +
            "<align=\"left\">" +
            "After pressing the <color=#FF6060FF>reset</color> button, " +
            "you should <color=#90B0FFFF>restart</color> this client."
            );
        root.Child("Count").SetValueAsync(0);
        root.Child("GameState").SetValueAsync(0);
        root.Child("ReadyPlayers").SetValueAsync(0);
        root.Child("Players").SetValueAsync(0);

    }

    /*=============================================================================================
     *  MAIN GAME LOOP update functions
     *============================================================================================*/
    
    private PlayerInstance[] players;
    // player role codes
    public enum Role { civilian, wolf, medic, sheriff, exiled, killed };
    // reference to player card GUI panels set in the inspector
    public List<GameObject> playerCards;
    // reference to the main panel set in the inspector
    public GameObject mainPanel;
    // reference to the timer panel set in the insprctor
    public GameObject timerPanel;
    // timer default values set in the inspector
    public float timeLimit_Discussion;
    public float timeLimit_Convict;
    public float timeLimit_Defend;
    public float timeLimit_Night;
    // sync token between sub turn routines
    private int phaseSyncValue;
    // turn number incremented on every new day start
    private int turnNum = 0;
    private GameObject mainPanelText;
    private const int hardcodedPlayerCountRequirement = 4;
    IEnumerator LobbyUpdate()
    {
        mainPanelText = mainPanel.transform.GetChild(1).gameObject;
        while (true) // main game loop
        {
            Debug.Log("Game loop start (lobby update iteration)");
            phaseSyncValue = 0;
            // hard coded player capacity temporarily for demo purposes
            players = new PlayerInstance[hardcodedPlayerCountRequirement];
            StartCoroutine(JoinPhase()); // phase 1
            yield return new WaitWhile(() => phaseSyncValue == 0); // wait for next phase
            while (true) // turn loop
            {
                StartCoroutine(DayPhase()); // phase 2
                yield return new WaitWhile(() => phaseSyncValue == 1);
                StartCoroutine(EvePhase()); // phase 3
                yield return new WaitWhile(() => phaseSyncValue == 2);
                StartCoroutine(NightPhase()); //phase 4
                yield return new WaitWhile(() => phaseSyncValue == 3);
                phaseSyncValue = 1;
            }
        }
        
    }
    /*=============================================================================================
     *  PHASE 4
     *============================================================================================*/
     private IEnumerator NightPhase()
    {
        // debug text update
        debugTextObj.GetComponent<PanelTextControl>().SetNewText(
           "[Night Phase]\n" +
           "<align=\"left\">" +
           "In this phase night time activities take place. Mafias choose who to kill, Medics choose who to save, and Sherifs choose who to check.\n" +
           "Not much actually goes on on the lobby screen. Mobile clients will handle sheriff locally." +
           " Mafia and medics will simply pass poll value of player ID chosen. Currently assumes only one of each role."
           );
        // switch to night background
        gameObject.GetComponent<BGHandler>().SwitchToBG(3);
        // main panel flavor text update for evening phase
        mainPanelText.GetComponent<PanelTextControl>().SetNewText("<color=#FFA070FF>Day " + turnNum + "</color>" +
            "\nThe night has begun.\n<color=#FFA070FF>Clandestine activities</color> take place...");
        yield return new WaitForSeconds(1.5f);
        timerPanel.GetComponent<PopupAnimate>().AnimOpen();
        yield return new WaitForSeconds(1f);
        timerPanel.GetComponent<TimerController>().SetTimer(timeLimit_Night);
        yield return new WaitForSeconds(1.5f);
        timerPanel.GetComponent<TimerController>().StartTimer();
        foreach (PlayerInstance player in players)
        {
            
            if (player.role < Role.exiled)
            {
                player.playerCard.GetComponent<PopupAnimate>().AnimUnFocus();
                player.updatePoll = true;
                player.playerRoot.Child("poll").SetValueAsync(0);
            }
        }
        root.Child("GameState").SetValueAsync(3);
        yield return new WaitUntil(() => timerPanel.GetComponent<TimerController>().getIsDone());
        timerPanel.GetComponent<PopupAnimate>().AnimClose();
        int killChoice = 0, saveChoice = 0;
        foreach (PlayerInstance player in players)
        { // check all the wolves and medics for selected players
            if (player.role == Role.wolf)
            {
                killChoice = player.poll;
                //player.statusText.SetNewText("Kill(" + killChoice + ")");
            }
            else if (player.role == Role.medic)
            {
                saveChoice = player.poll;
                //player.statusText.SetNewText("Save(" + saveChoice + ")");
            }
        }
        if (killChoice == saveChoice) killChoice = 0; // medic saved
        if (killChoice == 0) // nobody was killed
        {
            mainPanelText.GetComponent<PanelTextControl>().SetNewText("<color=#FFA070FF>Day " + turnNum + "</color>" +
           "\nDawn approaches.\nThe night was <color=#FFA070FF>seemingly uneventful</color>...");
        }
        else
        {
            PlayerInstance toBeKilled = players[killChoice - 1];
            toBeKilled.updatePoll = false;
            toBeKilled.SetRole(Role.killed);
            toBeKilled.subText.SetNewText("<color=#FFA070FF>Killed</color>");
            toBeKilled.statusText.SetNewText(" ");
            toBeKilled.playerCard.GetComponent<PopupAnimate>().AnimLowfocus();
            mainPanelText.GetComponent<PanelTextControl>().SetNewText("<color=#FFA070FF>Day " + turnNum + "</color>" +
           "\nDawn approaches.\n" +
           "<color=#FFA070FF>"+toBeKilled.name+"</color> was found <color=#FFA070FF>dead</color>!\n" +
           "The wolves struck, and no one could save them...");
        }
        yield return new WaitForSeconds(2f);
        
        phaseSyncValue++;
    }
    /*=============================================================================================
     *  PHASE 3
     *============================================================================================*/
    private IEnumerator EvePhase()
    {
        // debug text update
        debugTextObj.GetComponent<PanelTextControl>().SetNewText(
           "[Evening Phase]\n" +
           "<align=\"left\">" +
           "In this phase the Lobby waits until every player has voted for a <color=#FFA070FF>target player</color>.\n" +
           "If there is a majority, the Lobby advances <color=#FFA070FF>GameState to 4</color> and enters a 'yea-nay' vote.\n" +
           "Else, the Lobby advances <color=#FFA070FF>GameState to 3</color> and goes to [Night phase]"
           );
        // switch to evening background
        gameObject.GetComponent<BGHandler>().SwitchToBG(2);
        // main panel flavor text update for evening phase
        mainPanelText.GetComponent<PanelTextControl>().SetNewText("<color=#FFA070FF>Day " + turnNum + "</color>" +
            "\nThe sun is setting.\nThe townsfolk must decide who to <color=#FFA070FF>convict</color>...");
        yield return new WaitForSeconds(1.5f);
        timerPanel.GetComponent<PopupAnimate>().AnimOpen();
        yield return new WaitForSeconds(1f);
        timerPanel.GetComponent<TimerController>().SetTimer(timeLimit_Convict);
        yield return new WaitForSeconds(1.5f);
        timerPanel.GetComponent<TimerController>().StartTimer();
        int numVoted = 0, inPlay = 0;
        foreach (PlayerInstance player in players)
        {
            // Only counts players still in play
            if (player.role < Role.exiled)
            {
                //player.playerCard.GetComponent<PopupAnimate>().AnimBump();
                player.statusText.SetNewText(" ");
                player.updateRoutine = StartCoroutine(UpdatePlayer(player));
                inPlay++;
            }
        }
        IEnumerator UpdatePlayer(PlayerInstance player)
        {
            yield return new WaitUntil(() => player.poll != 0 || timerPanel.GetComponent<TimerController>().getIsDone());
            player.updatePoll = false; // maintain currently retrieved poll values
            if (player.poll != 0)
            {
                numVoted++;
                PlayerInstance target = players[player.poll - 1];
                Debug.Assert(target != player && target.role < Role.exiled); // you cant vote for yourself or the dead!
                player.statusImg.SwitchToImg(player.poll);
                player.subText.SetNewText(target.name);
            }
            // wait while timer is running and then clean up the listener and reset some stuff
            yield return new WaitUntil(() => (timerPanel.GetComponent<TimerController>().getIsDone() || numVoted >= inPlay));
            player.statusImg.SwitchToImg(0); // Set status image to transparent
            player.subText.SetNewText("- - -"); // reset some gui text
            player.statusText.SetNewText(" ");
            player.playerRoot.Child("poll").SetValueAsync(0);   
        }
    
        // synchronously advance gamestate to inform mobile clients to switch to daytime voting activity
        root.Child("GameState").SetValueAsync(2);
        // wait until voting complete or timeout
        yield return new WaitUntil(() => (timerPanel.GetComponent<TimerController>().getIsDone() || numVoted >= inPlay));
        timerPanel.GetComponent<PopupAnimate>().AnimClose();
        // count up who was chosen
        int[] tally = new int[players.Length+1];
        foreach (PlayerInstance player in players) // tally up votes of each inPlay player
        {
            if (player.role >= Role.exiled) continue;
            tally[player.poll]++;
            player.poll = 0; // reset poll value once read
        }
        var topPlayer = (indx: 0, count: 0);
        for (int i=0; i<tally.Length; i++)
        {
            if (tally[i] > topPlayer.count) // new top player
                topPlayer = (i, tally[i]);
            else if (tally[i] == topPlayer.count) // tie
                topPlayer = (0, tally[i]); // ties dont count
        }
        if (topPlayer.indx == 0) // no majority
        {
            mainPanelText.GetComponent<PanelTextControl>().SetNewText("<color=#FFA070FF>Day " + turnNum + "</color>" +
            "\nNo majority was found.\nThere will be no one <color=#FFA070FF>exiled</color> today.");
            yield return new WaitForSeconds(2f);
            // move to night phase (gamestate 3)
        }
        else // there was a majority chosen player to convict
        { //defense subphase
            foreach (PlayerInstance player in players) player.statusImg.SwitchToImg(0);
            debugTextObj.GetComponent<PanelTextControl>().SetNewText(
           "[Defense SubPhase]\n" +
           "<align=\"left\">" +
           "Player on trial gets some time to defend, then lobby advances GameState to 4." +
           " This signals a yea-nay vote on the mobile side. There is no timer, everyone" +
           " must vote. A majority yea means the player is 'exiled'. Then GameState goes to 4 for night."
           );
            PlayerInstance onTrial = players[topPlayer.indx-1];
            mainPanelText.GetComponent<PanelTextControl>().SetNewText("<color=#FFA070FF>Day " + turnNum + "</color>\n" +
           "<color=#FFA070FF>"+onTrial.name+"</color> must stand trial.\n" +
           "The townsfolk hear their <color=#FFA070FF>defense</color>.");
            yield return new WaitForSeconds(2f);
            // update player ui for defense subphase
            onTrial.playerCard.GetComponent<PopupAnimate>().AnimFocus();
            onTrial.subText.SetNewText("Defending");
            onTrial.updatePoll = true;
            onTrial.playerRoot.Child("poll").SetValueAsync(-1);
            yield return new WaitUntil(() => onTrial.poll == -1);
            onTrial.updatePoll = false;
            foreach (PlayerInstance juryMember in players)
            { // for all members of the jury
                if (juryMember.role >= Role.exiled || juryMember.poll==-1) continue;
                juryMember.updatePoll = true;
                juryMember.playerRoot.Child("poll").SetValueAsync(0);
                juryMember.playerCard.GetComponent<PopupAnimate>().AnimLowfocus();
                juryMember.subText.SetNewText("On Jury");
            }
            timerPanel.GetComponent<PopupAnimate>().AnimOpen(); // start up timer
            yield return new WaitForSeconds(1f);
            timerPanel.GetComponent<TimerController>().SetTimer(timeLimit_Defend);
            yield return new WaitForSeconds(1.5f);
            timerPanel.GetComponent<TimerController>().StartTimer();
            yield return new WaitUntil(() => timerPanel.GetComponent<TimerController>().getIsDone());
            timerPanel.GetComponent<PopupAnimate>().AnimClose(); // after timer is done
            mainPanelText.GetComponent<PanelTextControl>().SetNewText("<color=#FFA070FF>Day " + turnNum + "</color>\n" +
            "<color=#FFA070FF>" + onTrial.name + "</color> gave his case.\n" +
            "The townsfolk must vote <color=#FFA070FF>yea or nay</color> to exile.");
            yield return new WaitForSeconds(2f);
            root.Child("GameState").SetValueAsync(4); // gamestate 4 = yaynay vote activity
            int voted = 0, jurySize = 0, shouldExile = 0;
            foreach (PlayerInstance juryMember in players)
            { // for all members of the jury
                if (juryMember.role >= Role.exiled || juryMember.poll == -1) continue;
                juryMember.playerCard.GetComponent<PopupAnimate>().AnimUnFocus();
                juryMember.subText.SetNewText("Voting");
                jurySize++;
                juryMember.updateRoutine = StartCoroutine(UpdateJuryPlayer(juryMember));
            }
            IEnumerator UpdateJuryPlayer(PlayerInstance juryMember)
            {
                yield return new WaitUntil(() => juryMember.poll != 0);
                juryMember.updatePoll = false;
                string newStatus = ". . .";
                switch (juryMember.poll)
                {
                    case 1: newStatus = "<color=#FFA070FF>Yea</color>"; shouldExile++; break;
                    case 2: newStatus = "<color=#70A0FFFF>Nay</color>"; shouldExile--; break;
                    default: newStatus = "Error("+juryMember.poll+")"; break; // wut how
                }
                juryMember.poll = 0;
                voted++;
                juryMember.statusText.SetNewText(newStatus);
                yield return new WaitUntil(() => voted >= jurySize);
                juryMember.updatePoll = true;
                onTrial.playerRoot.Child("poll").SetValueAsync(0);
                onTrial.playerCard.GetComponent<PopupAnimate>().AnimUnFocus();
                juryMember.statusText.SetNewText(" ");
                juryMember.subText.SetNewText("- - -");

            }
            yield return new WaitUntil(() => voted >= jurySize);
            // yea nay votes are in
            string sentance = shouldExile > 0 ? "<color=#FFA070FF>Guilty</color>" : "<color=#70A0FFFF>Innocent</color>";
            mainPanelText.GetComponent<PanelTextControl>().SetNewText("<color=#FFA070FF>Day " + turnNum + "</color>\n" +
           "<color=#FFA070FF>" + onTrial.name + "</color> was found " + sentance + "!");
            if (shouldExile > 0)
            {
                onTrial.updatePoll = false;
                onTrial.SetRole(Role.exiled);
                onTrial.subText.SetNewText("<color=#FFA070FF>Exiled</color>");
                onTrial.statusText.SetNewText(" ");
                onTrial.playerCard.GetComponent<PopupAnimate>().AnimLowfocus();
            }
            else
            {
                onTrial.updatePoll = true;
                onTrial.playerRoot.Child("poll").SetValueAsync(0);
                onTrial.subText.SetNewText("- - -");
                onTrial.statusText.SetNewText(" ");
                onTrial.playerCard.GetComponent<PopupAnimate>().AnimUnFocus();
            }
            yield return new WaitForSeconds(1.5f);
        }
        phaseSyncValue++;
    }   

    /*=============================================================================================
     *  PHASE 2 -- fixed?
     *============================================================================================*/
    private IEnumerator DayPhase()
    {
        turnNum++; // new day means new turn
        debugTextObj.GetComponent<PanelTextControl>().SetNewText( // display some meta info in the debug panel
            "[Day Phase]\n" +
            "<align=\"left\">" +
            "This phase simply waits a bit and then changes <color=#FFA070FF>GameState to 2</color>.\n" +
            "The Lobby will <color=#FFA070FF>skip the countdown</color> if all players vote do so in the discussion activity."
            );
        gameObject.GetComponent<BGHandler>().SwitchToBG(1); // transition to next background
        mainPanelText.GetComponent<PanelTextControl>().SetNewText("<color=#FFA070FF>Day " + turnNum + "</color>" +
            "\nThe town hall convenes at dawn.\n<color=#FFA070FF>Discussion</color> ensues...");
        yield return new WaitForSeconds(2f);
        root.Child("GameState").SetValueAsync(1);
        timerPanel.GetComponent<PopupAnimate>().AnimOpen();
        yield return new WaitForSeconds(1f);
        timerPanel.GetComponent<TimerController>().SetTimer(timeLimit_Discussion);
        yield return new WaitForSeconds(1.5f);
        timerPanel.GetComponent<TimerController>().StartTimer();
        // wait until timer expires or all in-play players vote to pass time
        int numPass=0, inPlay=0, numWolves=0;
        foreach (PlayerInstance player in players) // start up player update routines
        {
            // only include players who are still in play
            if (player.role < Role.exiled)
            {
                player.updatePoll = true;
                player.statusText.SetNewText(" ");
                player.updateRoutine = StartCoroutine(UpdatePlayer(player));
                inPlay++;
                if (player.role == Role.wolf) numWolves++;
            }
        }
        IEnumerator UpdatePlayer(PlayerInstance player)
        {
            yield return new WaitUntil(() => player.poll != 0 || timerPanel.GetComponent<TimerController>().getIsDone());
            if (player.poll != 0)
            {
                int currentPoll = player.poll;
                if (currentPoll == 1)
                {
                    player.statusText.SetNewText("<color=#FFA070FF>Pass</color>");
                    numPass++;
                }
                else player.statusText.SetNewText("Error(" + currentPoll + ")");
            }
            yield return new WaitUntil(() => (timerPanel.GetComponent<TimerController>().getIsDone() || numPass >= inPlay));
            // reset player poll and status
            player.playerRoot.Child("poll").SetValueAsync(0);
            player.statusText.SetNewText(". . .");
        }
        // wait until we are done
        yield return new WaitUntil(() => (timerPanel.GetComponent<TimerController>().getIsDone() || numPass >= inPlay));
        // close the timer panel
        timerPanel.GetComponent<PopupAnimate>().AnimClose();
        if (numWolves == 0)
        {
            mainPanelText.GetComponent<PanelTextControl>().SetNewText("<color=#FFA070FF>Day " + turnNum + "</color>" +
           "\nThe wolves have all been exiled.\n<color=#FFA070FF>Civilians Win</color>!");
            gameOver();
        }
        else if (inPlay <= numWolves * 2)
        {
            mainPanelText.GetComponent<PanelTextControl>().SetNewText("<color=#FFA070FF>Day " + turnNum + "</color>" +
           "\nThe town hall slowly fills with sinister laughter.\n<color=#FFA070FF>Wolves Win</color>!");
            gameOver();
        }
        else
        {
            phaseSyncValue++;
        }
    }

    private void gameOver()
    {
        foreach (PlayerInstance player in players)
        {
            player.playerCard.GetComponent<PopupAnimate>().AnimUnFocus();
            player.statusText.SetNewText(" ");
            player.statusImg.SwitchToImg((int)player.originalRole + 7);
        }
    }

    /*=============================================================================================
     *  PHASE 1 -- fully functional
     *============================================================================================*/
    private IEnumerator JoinPhase()
    {
        int fullyJoined = 0 , numReady = 0;
        // add a listener to the players directory on the db to handle new players joining
        root.Child("Players").ValueChanged += HandleJoinPlayer;
        void HandleJoinPlayer(object sender, ValueChangedEventArgs args)
        {
            Debug.Log("HandleJoinPlayer event fired");
            if (args.DatabaseError != null) // Throw error message on listener exception
            {
                Debug.LogError(args.DatabaseError.Message);
                return;
            }

            if (!args.Snapshot.HasChildren) // snapshot has no players
            {
                for (int i = 0; i < players.Length; i++)
                {
                    // dont try to remove never connected players
                    if (players[i] == null) continue;
                    // animate closing the panel
                    playerCards[i].GetComponent<PopupAnimate>().AnimClose();
                    // clear player instance which is removed
                    players[i] = null;
                }
            }
            else // snapshot has players and we allow new players to join
            {
                for (int i = 0; i < players.Length; i++)
                {
                    // dont reinitialize already connected players
                    if (players[i] != null) continue;
                    string playerDir = "Player" + (i + 1);
                    if (args.Snapshot.HasChild(playerDir)) // initialize new players
                    {
                        players[i] = new PlayerInstance(root.Child("Players/" + playerDir), playerCards[i]);
                        players[i].updateRoutine = StartCoroutine(UpdatePlayer(players[i]));
                    }
                }
            }
        }
        // wait until all 4 players have finished joining.
        yield return new WaitUntil(() => fullyJoined == hardcodedPlayerCountRequirement);
        // remove the player listener since everyone joined (remove this to allow more than 4 players, non-demo config)
        root.Child("Players").ValueChanged -= HandleJoinPlayer;
        // now update the message on the main panel
        mainPanelText.GetComponent<PanelTextControl>().SetNewText("" +
           "\n\nWaiting on players to <color=#A0FFA0FF>ready up</color> ");
        // wait until all 3 players ready up
        yield return new WaitUntil(() => numReady == hardcodedPlayerCountRequirement - 1);
        mainPanelText.GetComponent<PanelTextControl>().SetNewText("" +
           "\n\nWaiting on <color=#A0A0FFFF>VIP</color> to <color=#FFA070FF>start game</color> ");
        // wait until VIP hits start
        yield return new WaitUntil(() => gameState == 1);
        phaseSyncValue++;

        IEnumerator UpdatePlayer(PlayerInstance player)
        {
            // wait until player has fully initialized
            yield return new WaitWhile(() => player.syncState < 2);
            fullyJoined++; //notify the JoinPhase that this player has finished joining the lobby
            // open the player card on the gui
            player.playerCard.GetComponent<PopupAnimate>().AnimOpen();
            yield return new WaitUntil(() => player.poll != 0);
            int currentPoll = player.poll;
            if (currentPoll == 2)
            {
                player.statusText.SetNewText("<color=#A0A0FFFF>VIP</color>");
            }
            else if (currentPoll == 1)
            {
                player.statusText.SetNewText("<color=#A0FFA0FF>Ready</color>");
                numReady++;
            }
            else
            {
                player.statusText.SetNewText("Error("+currentPoll+")");
            }
            player.playerRoot.Child("poll").SetValueAsync(0);
            // wait until VIP hits start to reset text before handing player off to next phase
            yield return new WaitUntil(() => gameState == 1);
            player.FetchRole();
            player.statusText.SetNewText(". . .");
        }
    }

    /*=============================================================================================
     *  Local PLAYER INSTANCE Utilty Class
     *============================================================================================*/
    class PlayerInstance
    {
        // phase specific update routine for this player (set per phase)
        public Coroutine updateRoutine;
        // gui panel dedicated for this player (set on init)
        public GameObject playerCard;
        // reference to the player firebase directory (set on init)
        public DatabaseReference playerRoot;
        // local cache of database player fields (set on read cycles)
        public string name = "";
        public int icon = 0;
        public int poll = 0;
        public bool updatePoll = true;
        public Role originalRole = 0;
        public Role role = 0;
        // reference to player-specific gui elements for conveneience
        public PanelTextControl nameText;
        public PanelTextControl subText;
        public PanelTextControl statusText;
        public StatusImageController statusImg;
        GameObject iconImage;
        // token value for async operations used by routines
        public int syncState = 0;
        
        public PlayerInstance(DatabaseReference playerRoot, GameObject playerCard)
        {
            Debug.Log("Initializing a player instance");

            // get the GUI elements to update in the player card
            nameText = playerCard.transform.GetChild(0).GetComponent<PanelTextControl>();
            subText = playerCard.transform.GetChild(1).GetComponent<PanelTextControl>();
            statusText = playerCard.transform.GetChild(2).GetChild(0).GetComponent<PanelTextControl>();
            statusImg = playerCard.transform.GetChild(2).GetChild(1).GetComponent<StatusImageController>();
            iconImage = playerCard.transform.GetChild(3).gameObject;

            this.playerRoot = playerRoot;
            this.playerCard = playerCard;

            InitializeNameAndIcon();
            this.playerRoot.Child("poll").ValueChanged += PollUpdateListener;            
        }

        public void FetchRole() //only needed since mobile client sets role atm
        {
            playerRoot.Child("role").GetValueAsync().ContinueWith(task =>
            {
                if (task.IsFaulted)
                {
                    Debug.LogError("Player role get failed");
                    return;
                }
                else if (task.IsCompleted)
                {
                    role = originalRole = (Role)int.Parse(task.Result.Value.ToString());
                }
            });
        }

        private void PollUpdateListener(object sender, ValueChangedEventArgs args)
        {
            if (args.DatabaseError != null) // Throw error message on listener exception
            {
                Debug.LogError(args.DatabaseError.Message);
                return;
            }
            if (!updatePoll) return; // dont do anything if poll is not to be updated
            this.poll = int.Parse(args.Snapshot.Value.ToString());
        }

        private void InitializeNameAndIcon()
        {
            // get name from database
            playerRoot.Child("name").GetValueAsync().ContinueWith(task =>
            {
                if (task.IsFaulted)
                {
                    Debug.LogError("Player name get failed");
                    return;
                }
                else if (task.IsCompleted)
                {
                    // set name in player card
                    this.name = task.Result.Value.ToString();
                    this.nameText.SetNewText(this.name);
                    syncState++;
                }
            });
            // get icon index from database
            playerRoot.Child("icon").GetValueAsync().ContinueWith(task =>
            {
                if (task.IsFaulted)
                {
                    Debug.LogError("Icon index get failed");
                    return;
                }
                else if (task.IsCompleted)
                {
                    // set icon in player card
                    this.icon = int.Parse(task.Result.Value.ToString());
                    Debug.Log("Icon number retrieved: " + icon);
                    this.iconImage.GetComponent<Image>().sprite = GameObject.Find("SynchronizedScripts").GetComponent<PlayerIcons>().getIconForPlayer(icon);
                    syncState++;
                }
            });

        }

        public void SetRole(Role role)
        {
            this.role = role;
            playerRoot.Child("role").SetValueAsync((int)this.role);
        }

    }

    /*===========================================================================================*/




    /*=============================================================================================
     *  DEBUG PANEL update functions
     *============================================================================================*/
    private int playerCount = 0;
    public List<GameObject> countText;
    private void CountUpdate()
    {   
        root.Child("Count").ValueChanged += HandleValueChanged;
        void HandleValueChanged(object sender, ValueChangedEventArgs args)
        {
            if (args.DatabaseError != null) // Throw error message on listener exception
            {
                Debug.LogError(args.DatabaseError.Message);
                return;
            }
            int prevCount = this.playerCount; // note change in player count
            this.playerCount = int.Parse(args.Snapshot.Value.ToString()); // update local player count
            foreach (GameObject item in countText) // update passed text
            {
                if (item!=null) item.GetComponent<PanelTextControl>().SetNewText(playerCount.ToString());
            }
            Debug.Log("PlayerCount Value changed from " + prevCount + " to " + playerCount);           
        }
    }
    int readyPlayers=0;
    public List<GameObject> readyText;
    void ReadyCountUpdate()
    {
        root.Child("ReadyPlayers").ValueChanged += HandleValueChanged;
        void HandleValueChanged(object sender, ValueChangedEventArgs args)
        {
            if (args.DatabaseError != null) // Throw error message on listener exception
            {
                Debug.LogError(args.DatabaseError.Message);
                return;
            }
            readyPlayers = int.Parse(args.Snapshot.Value.ToString()); // update local gamestate
            foreach (GameObject item in readyText) // update passed text
            {
                if (item != null) item.GetComponent<PanelTextControl>().SetNewText(readyPlayers.ToString());
            }
        }
    }
    private int gameState = 0;
    public List<GameObject> gamestateText;
    void GamestateUpdate()
    {
        root.Child("GameState").ValueChanged += HandleValueChanged;
        void HandleValueChanged(object sender, ValueChangedEventArgs args)
        {
            if (args.DatabaseError != null) // Throw error message on listener exception
            {
                Debug.LogError(args.DatabaseError.Message);
                return;
            }
            this.gameState = int.Parse(args.Snapshot.Value.ToString()); // update local gamestate
            foreach (GameObject item in gamestateText) // update passed text
            {
                if (item!=null) item.GetComponent<PanelTextControl>().SetNewText(gameState.ToString());
            }
        }
    }

}

