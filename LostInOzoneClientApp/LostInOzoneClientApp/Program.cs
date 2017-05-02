using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using IronPython.Hosting;
using Microsoft.Scripting.Hosting;
using Newtonsoft.Json;

namespace LostInOzoneClientApp
{
   public class WebWorker
   {
      public async Task<string> GetAsync(RootObject rootObject)
      {
         string json = JsonConvert.SerializeObject(rootObject);
         using (HttpClient client = new HttpClient())
         {
            HttpContent content = new StringContent($"{json}\n");
            try {
               client.DefaultRequestHeaders.Add("UserAgent", "Dima");
               using (HttpResponseMessage response = await client.PostAsync("http://localhost:8999", content))
               {
                  if (response.IsSuccessStatusCode)
                  {
                     return await response.Content.ReadAsStringAsync();
                  }

                  return "";
               }
            }
            catch(Exception e)
            {
               throw;
            }
         }
      }
   }

   class Program
   {
      static void Main(string[] args)
      {
         RootObject pass = new RootObject()
         {
            source = new Location()
            {
               latitude = 53.9045,
               longitude = 27.5615
            },
            destination = new Location()
            {
               latitude = 51.5074,
               longitude = 0.1278
            }
         };
         WebWorker webWorker = new WebWorker();
         webWorker.GetAsync(pass);
         //ScriptEngine engine = Python.CreateEngine();
         //ScriptScope scope = engine.CreateScope();
         //var t = engine.GetSearchPaths();
         ////engine.SetSearchPaths(new List<string>() {@"d:\Anaconda\Lib\"});
         //engine.ExecuteFile(@"d:\projects\LostInOzone\lost_in_ozone\LostInOzonServer\PythonServer\LostInOzon.py", scope);
         //dynamic function = scope.GetVariable("main");
         //dynamic result = function(53.2f, 27f);
         Console.Read();
      }
   }

   public class Location
   {
      public string name { get; set; }
      public double latitude { get; set; }
      public double longitude { get; set; }
   }

   public class Point
   {
      public double latitude { get; set; }
      public double longitude { get; set; }
      public double doze { get; set; }
      public int height { get; set; }
      public int speed { get; set; }
   }

   public class Path
   {
      public Point point { get; set; }
   }

   public class RootObject
   {
      public Location source { get; set; }
      public Location destination { get; set; }
      public List<Path> path { get; set; }
   }
}
