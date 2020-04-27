using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class PlayerIcons : MonoBehaviour
{
    public List<Sprite> icons;
    public Sprite getIconForPlayer(int i)
    {
        return i <= icons.Count ? icons[i - 1] : null;
    }
}
