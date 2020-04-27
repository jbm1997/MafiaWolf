using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class StatusImageController : MonoBehaviour
{
    private GameObject[] statusImgObj;
    public int curImg = 0;

    void Start()
    {
        statusImgObj = new GameObject[transform.childCount];
        // initialize status image objects from transform hierarchy
        for (int i=0; i<statusImgObj.Length; i++)
        {
            statusImgObj[i] = transform.GetChild(i).gameObject;
        }
    }
    public void ImgSwitchTest()
    {
        SwitchToImg((curImg + 1) % statusImgObj.Length);
    }

    public void SwitchToImg(int i) 
    {
        if (i < statusImgObj.Length && i!=curImg)
        {
            statusImgObj[curImg].GetComponent<Animator>().Play("BumpFadeOut");
            curImg = i;
            statusImgObj[curImg].GetComponent<Animator>().Play("BumpFadeIn");
        }
    }
}
