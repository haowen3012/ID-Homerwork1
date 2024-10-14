import requests
from bs4 import BeautifulSoup
import os
import re
import time


"""Shows the first 200 papers on Pose Estimation (filter by title)"""
input_link = "https://arxiv.org/search/?query=Pose+Estimation&searchtype=title&abstracts=show&order=-announced_date_first&size=200"


def clean_filename(filename):
    """Rimuove caratteri non validi per i nomi dei file."""
    return re.sub(r'[\/:*?"<>|]', '', filename).strip()


def is_valid_html(url):
    """Controlla se l'URL contiene un documento HTML."""
    try:
        response = requests.head(url)
        return 'text/html' in response.headers.get('Content-Type', '').lower()
    except requests.RequestException:
        return False


def download_html(title, url, index, output_dir):
    """Scarica il contenuto HTML di un paper da un link e lo salva come file."""
    try:
        response = requests.get(url, timeout=60)
        response.raise_for_status()  # raise an exception for 4xx and 5xx errors
        
        safe_title = clean_filename(title)
        file_name = f"{index}_{safe_title}.html"
        file_path = os.path.join(output_dir, file_name)
        
        with open(file_path, "w", encoding="utf-8") as file:
            file.write(response.text)
        print(f"saved: {file_path}")
        return True  # download success
    except requests.Timeout:
        print(f"download failed {title}: Timeout after 60 seconds")
        return False  # request timeout
    except requests.HTTPError as e:
        print(f"download failed {title}: {e}")
        return False  # download failed

def extract_papers_from_link(link, max_results=600):
    """Estrae i link ai paper associati dall'URL fornito e restituisce i loro link HTML."""
    response = requests.get(link)
    response.raise_for_status()

    soup = BeautifulSoup(response.text, 'html.parser')
    paper_links = []

    # Trova tutti i link ai paper nel documento
    for a in soup.find_all('a', href=True):
        href = a['href']
        if '/abs/' in href:  # Filtra solo i link ai paper
            # Sostituisce /abs/ con /html/
            html_link = href.replace('/abs/', '/html/')
            paper_links.append((a.text.strip(), html_link))  # Aggiungi il titolo e il link

    return paper_links[:max_results]  # Restituisce solo i primi max_results

def main():
    # URL di partenza fornito
    # input_link = input("Inserisci l'URL da cui estrarre i paper: ")
    
    # Directory dove salvare i file HTML
    output_dir = "ID-Homerworks\homework1\Pose_Estimation\sources_PE"
    os.makedirs(output_dir, exist_ok=True)
    
    # Extract papers from the input link ( first 200 and next 200)
    papers = extract_papers_from_link(input_link) + extract_papers_from_link(input_link + "&start=200")

    # Scrittura sul file txt
    with open('read_me.txt', 'a', encoding='utf-8') as readme_file:
        readme_file.write(f"Lista dei documenti HTML scaricati: \n\n")

    # Scarica i documenti HTML solo se validi
    download_count = 0
    for index, (title, html_link) in enumerate(papers, start=1):
        if is_valid_html(html_link):  # Verifica che il link sia HTML valido
            if download_html(title, html_link, index, output_dir):  # Scarica e controlla se è riuscito
                download_count += 1
                with open('read_me.txt', 'a', encoding='utf-8') as readme_file:
                    print(f'downloaded: {html_link}')
                    readme_file.write(f"{index}. Titolo: {title} - Link: {html_link}\n")
                if download_count >= 300:  # Ferma i download al raggiungimento di 300 file
                    print("300 papers limit reached.")
                    break
                time.sleep(2)  # Add a delay of 2 second between requests

if __name__ == "__main__":
    main()