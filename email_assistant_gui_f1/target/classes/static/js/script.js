/**
 * 
 */
function generateEmailReply() {
     const emailContent = document.getElementById("emailContent").value;
     const tone = document.getElementById("tone").value;

     document.getElementById("loader").style.display = "block";
     document.getElementById("reply").style.display = "none";
     document.getElementById("copyBtn").style.display = "none";

     fetch("http://localhost:8080/api/email/generate", {
       method: "POST",
       headers: { "Content-Type": "application/json" },
       body: JSON.stringify({ emailContent, tone })
     })
     .then(response => response.text())
     .then(reply => {
       const replyDiv = document.getElementById("reply");
       replyDiv.innerText = reply;
       replyDiv.style.display = "block";
       replyDiv.classList.add("animate__fadeInUp");
       document.getElementById("copyBtn").style.display = "inline-block";
       document.getElementById("copyBtn").classList.add("animate__fadeIn");
       document.getElementById("loader").style.display = "none";
     })
     .catch(error => {
       alert("Error: " + error);
       document.getElementById("loader").style.display = "none";
     });
   }

   function copyToClipboard() {
     const replyText = document.getElementById("reply").innerText;
     navigator.clipboard.writeText(replyText).then(() => {
       const successMsg = document.getElementById("successMessage");
       successMsg.style.display = "block";
       successMsg.classList.add("animate__slideInRight");
       setTimeout(() => {
         successMsg.style.display = "none";
       }, 3000);
     }).catch((error) => {
       alert("Failed to copy text: " + error);
     });
   }