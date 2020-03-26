using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class PopupAnimate : MonoBehaviour
{
    Animator anim;
    void Start()
    {
        anim = gameObject.GetComponent<Animator>();
    }

    public void AnimOpen()
    {
        if (anim.GetCurrentAnimatorStateInfo(0).IsName("Closed"))
        {
            anim.Play("OpenPopup");
        }
    }

    public void AnimClose()
    {
        if (anim.GetCurrentAnimatorStateInfo(0).IsName("Opened"))
        {
            anim.Play("ClosePopup");
        }
    }
}
