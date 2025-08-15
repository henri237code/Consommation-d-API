using ActeursWeb.Models;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Threading.Tasks;
using System.Web;
using System.Web.Mvc;



namespace ActeursWeb.Controllers
{
    public class ActeurController : Controller
    {
        private readonly string apiUrl = "http://127.0.0.1:8000";

        public async Task<ActionResult> Index()
        {
            using (HttpClient client = new HttpClient())
            {
                var response = await client.GetStringAsync($"{apiUrl}/acteurs");
                var acteurs = JsonConvert.DeserializeObject<List<Acteur>>(response);
                return View(acteurs);
            }
        }

        public async Task<ActionResult> Stats()
        {
            using (HttpClient client = new HttpClient())
            {
                var response = await client.GetStringAsync($"{apiUrl}/total_acteurs");
                var stats = JsonConvert.DeserializeObject<Dictionary<string, int>>(response);
                ViewBag.Total = stats["total"];
                return View();
            }
        }

        [HttpGet]
        public ActionResult Ajouter()
        {
            return View();
        }
       
       

        
        [HttpPost]
        public async Task<ActionResult> Ajouter(Acteur acteur)
        {
            using (HttpClient client = new HttpClient())
            {
                var json = JsonConvert.SerializeObject(acteur);
                var content = new StringContent(json, System.Text.Encoding.UTF8, "application/json");
                var response = await client.PostAsync("http://127.0.0.1:8000/acteur/", content);


                if (response.IsSuccessStatusCode)
                {
                    return RedirectToAction("Index"); 
                }
                else
                {
                    ViewBag.Error = "Erreur lors de l'ajout.";
                    return View();
                }
            }
        }
        [HttpPost]
        public async Task<ActionResult> Supprimer(int id)
        {
            using (HttpClient client = new HttpClient())
            {
                var response = await client.DeleteAsync($"{apiUrl}/acteur/{id}");
                if (response.IsSuccessStatusCode)
                    return new HttpStatusCodeResult(200);
                else
                    return new HttpStatusCodeResult(500);
            }
        }


        [HttpPost]
        public async Task<ActionResult> Modifier(Acteur acteur)
        {
            using (HttpClient client = new HttpClient())
            {
                var json = JsonConvert.SerializeObject(acteur);
                var content = new StringContent(json, System.Text.Encoding.UTF8, "application/json");
                var response = await client.PutAsync($"{apiUrl}/acteur/{acteur.id}", content);

                if (response.IsSuccessStatusCode)
                    return new HttpStatusCodeResult(200);
                else
                    return new HttpStatusCodeResult(500);
            }
        }


    }
}