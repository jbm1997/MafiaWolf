using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class PopupAnimate : MonoBehaviour
{
    private Animator anim;
    private bool isAnimating = false;
    void Start()
    {
        anim = gameObject.GetComponent<Animator>();
    }

    private bool IsCurState(string name)
    {
        return GetCurState().IsName(name);
    }

    private AnimatorStateInfo GetCurState()
    {
        return anim.GetCurrentAnimatorStateInfo(0);
    }

    public void AnimOpen()
    {
        StartCoroutine(AnimOpen());
        IEnumerator AnimOpen()
        {
            yield return new WaitWhile(() => isAnimating);
            isAnimating = true;
            if (IsCurState("Closed"))
            {
                anim.Play("OpenPopup");
                yield return new WaitUntil(() => IsCurState("Opened"));
            }
            isAnimating = false;
        }
    }

    public void AnimClose()
    {
        StartCoroutine(AnimClose());
        IEnumerator AnimClose()
        {
            yield return new WaitWhile(() => isAnimating);
            isAnimating = true;
            if (GetCurState().IsName("Focused"))
            {
                anim.Play("DeFocusPopup");
                yield return new WaitUntil(() => IsCurState("Opened"));
            }
            else if (GetCurState().IsName("Lowfocused"))
            {
                anim.Play("DeLowfocusPopup");
                yield return new WaitUntil(() => IsCurState("Opened"));
            }
            if (GetCurState().IsName("Opened"))
            {
                anim.Play("ClosePopup");
                yield return new WaitUntil(() => IsCurState("Closed"));
            }
            isAnimating = false;
        }
    }

    public void AnimToggle()
    {
        if (IsCurState("Closed")) this.AnimOpen();
        else this.AnimClose();
    }

    public void AnimFocus()
    {
        StartCoroutine(AnimFocus());
        IEnumerator AnimFocus()
        {
            yield return new WaitWhile(() => isAnimating);
            isAnimating = true;
            if (GetCurState().IsName("Lowfocused"))
            {
                anim.Play("DeLowfocusPopup");
                yield return new WaitUntil(() => IsCurState("Opened"));
            }
            if (GetCurState().IsName("Opened"))
            {
                anim.Play("FocusPopup");
                yield return new WaitUntil(() => IsCurState("Focused"));
            }
            isAnimating = false;
        }
    }

    public void AnimLowfocus()
    {
        StartCoroutine(AnimLowfocus());
        IEnumerator AnimLowfocus()
        {
            yield return new WaitWhile(() => isAnimating);
            isAnimating = true;
            if (GetCurState().IsName("Focused"))
            {
                anim.Play("DeFocusPopup");
                yield return new WaitUntil(() => IsCurState("Opened"));
            }
            if (GetCurState().IsName("Opened"))
            {
                anim.Play("LowfocusPopup");
                yield return new WaitUntil(() => IsCurState("Lowfocused"));
            }
            isAnimating = false;
        }
    }

    public void AnimUnFocus()
    {
        StartCoroutine(AnimUnFocus());
        IEnumerator AnimUnFocus()
        {
            yield return new WaitWhile(() => isAnimating);
            isAnimating = true;
            if (GetCurState().IsName("Focused"))
            {
                anim.Play("DeFocusPopup");
            }
            else if (GetCurState().IsName("Lowfocused"))
            {
                anim.Play("DeLowfocusPopup");
            }
            yield return new WaitUntil(() => IsCurState("Opened"));
            isAnimating = false;
        }
    }

    public void AnimBump()
    {
        StartCoroutine(AnimBump());
        IEnumerator AnimBump()
        {
            yield return new WaitWhile(() => isAnimating);
            isAnimating = true;
            if (GetCurState().IsName("Opened"))
            {
                anim.Play("BumpPopup");
            }
            yield return new WaitUntil(() => IsCurState("Opened"));
            isAnimating = false;
        }
    }
}
