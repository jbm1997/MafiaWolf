using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class BGHandler : MonoBehaviour
{
    public List<GameObject> backgroundImages;
    public GameObject bgParticles;
    int currentImage = 0;

    public void NextBG()
    {
        SwitchToBG((currentImage + 1) % backgroundImages.Count);
    }

    public void SwitchToBG(int i)
    {
        if (i<backgroundImages.Count)
        {
            backgroundImages[currentImage].GetComponent<Animator>().Play("BumpFadeOut");
            currentImage = i;
            backgroundImages[currentImage].GetComponent<Animator>().Play("BumpFadeIn");
            StartCoroutine(ParticleAnimate());
        }
    }

    IEnumerator ParticleAnimate()
    {
        var bgParticlesComp = bgParticles.GetComponent<ParticleSystem>();
        var main = bgParticlesComp.main;
        var vel = bgParticlesComp.velocityOverLifetime;
        var emi = bgParticlesComp.emission;
        // stop emitting new particles when changing background
        emi.enabled = false;
        // setup a burst for when we reenable particles
        emi.SetBursts( new [] {new ParticleSystem.Burst(0f, 100)});
        main.startSpeedMultiplier *= 8f;
        main.startLifetimeMultiplier /= 8f;
        // multiply velocity of current particles by 10
        vel.speedModifierMultiplier *= 10f;
        // wait until background picture half done shifting to burst
        yield return new WaitForSeconds(0.25f);
        emi.enabled = true;
        // wait until burst occurs
        yield return new WaitForSeconds(0.045f);
        // remove burst
        emi.SetBursts(new ParticleSystem.Burst[0]);
        main.startSpeedMultiplier /= 8f;
        main.startLifetimeMultiplier *= 8f;
        // return the velocity to normal over 3 second
        while (vel.speedModifierMultiplier > 1)
        {
            vel.speedModifierMultiplier -= 3f*Time.deltaTime;
            yield return null;
        }

    }

}
