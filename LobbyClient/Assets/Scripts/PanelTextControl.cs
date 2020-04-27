using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;

public class PanelTextControl : MonoBehaviour
{
    private string nextText = "";

    public void SetNewText(string newText)
    {
        StartCoroutine(TextTransition(newText));
    }

    public void TextShift() // called by the animation event
    {
        gameObject.GetComponent<TextMeshProUGUI>().SetText(nextText);
        nextText = "";
    }

    IEnumerator TextTransition(string newText)
    {
        while (!nextText.Equals("")) yield return null; // wait until previous text transition is commited
        this.nextText = newText;
        // wait until previous animation is complete
        while (gameObject.GetComponent<Animator>().GetCurrentAnimatorStateInfo(0).IsName("TextTransition"))
            yield return null;
        gameObject.GetComponent<Animator>().Play("TextTransition");
    }
}
