using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using TMPro;

public class TimerController : MonoBehaviour
{
    private TextMeshProUGUI timerText;
    private Image timerRing;
    private float countFrom = 0; //number of seconds to start at
    private float timeLeft = 0; //current count

    private Coroutine textUpdate;
    private Coroutine ringUpdate;

    private bool isDone; //true once timer finishes, reset on setTimer

    public bool getIsDone()
    {
        return isDone;
    }

    void Start()
    {
        timerText = gameObject.transform.GetChild(2).GetComponent<TextMeshProUGUI>();
        timerRing = gameObject.transform.GetChild(1).GetComponent<Image>();
    }

    public void SetTimer(float countFrom)
    {
        HaltTimer();
        this.countFrom = countFrom;
        isDone = false;
        timerText.gameObject.GetComponent<PanelTextControl>().SetNewText(MinSecFormat(countFrom));
        StartCoroutine(RefillRing(1f));
    }

     IEnumerator RefillRing(float speed)
    {
        float progress = timerRing.fillAmount;
        while (progress<=1) // do every frame until ring is refilled
        {
            timerRing.fillAmount = progress;
            progress += Time.deltaTime * speed;
            yield return null;
        }
        timerRing.fillAmount = 1;
    }
    private string MinSecFormat(float totalSeconds)
    {
        int minutes = (int)(totalSeconds / 60f);
        int seconds = (int)(totalSeconds % 60f);
        return string.Format("{0:d1}:{1:d2}",minutes,seconds);
    }

    public void StartTimer()
    {
        if (textUpdate!=null || ringUpdate!=null || isDone)
        {
            Debug.Log("Aborted illegal attempt to start timer twice!");
            return;
        }
        timeLeft = countFrom;
        ToggleTimer();
    }

    public void ToggleTimer()
    {
        if (textUpdate != null || ringUpdate != null) HaltTimer();
        else
        {
            textUpdate = StartCoroutine(updateText());
            ringUpdate = StartCoroutine(updateRing());
        }
    }

    public void HaltTimer()
    {
        if (textUpdate != null) StopCoroutine(textUpdate);
        if (ringUpdate != null) StopCoroutine(ringUpdate);
        textUpdate = ringUpdate = null;
    }

    IEnumerator updateText()
    {
        string curText = MinSecFormat(timeLeft);
        while (timeLeft>0) //every frame until timer is done
        {
            string newText = MinSecFormat(timeLeft);
            if (!curText.Equals(newText)) //only update text on frames where it changes
            {
                timerText.SetText(newText);
                curText = newText;
            }
            timeLeft -= Time.deltaTime;
            yield return null;
        }
        isDone = true;
        textUpdate = null;
    }

    IEnumerator updateRing()
    {
        while (!isDone) // update ring every frame until timer is done
        {
            if (timeLeft>=0)
                timerRing.fillAmount = timeLeft / countFrom;
            yield return null;
        }
        timerRing.fillAmount = 0;
        ringUpdate = null;
    }
}
