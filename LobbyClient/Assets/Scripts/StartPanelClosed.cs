using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class StartPanelClosed : MonoBehaviour
{

    void Start()
    {
        // Start this panel closed
        gameObject.transform.localScale.Set(0f,0f,1f); // this doesn't actually work due to animator lock :(
        gameObject.GetComponent<Animator>().Play("Closed");
    }

}
