using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace ActeursWeb.Models
{
    public class Acteur
    {
        public int id { get; set; }
        public string name { get; set; }
        public string bio { get; set; }
        public string picture { get; set; }
    }
}