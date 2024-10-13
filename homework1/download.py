import requests
from bs4 import BeautifulSoup
import os
import re

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
        response = requests.get(url)
        response.raise_for_status()  # Alza un errore per risposte non valide
        
        safe_title = clean_filename(title)
        file_name = f"{index}_{safe_title}.html"
        file_path = os.path.join(output_dir, file_name)
        
        with open(file_path, "w", encoding="utf-8") as file:
            file.write(response.text)
        print(f"Salvato: {file_path}")
        return True  # Scaricamento completato con successo
    except requests.HTTPError as e:
        print(f"Errore nel download di {title}: {e}")
        return False  # Scaricamento fallito

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
    input_link = input("Inserisci l'URL da cui estrarre i paper: ")
    
    # Directory dove salvare i file HTML
    output_dir = "sources"
    os.makedirs(output_dir, exist_ok=True)
    
    # Estrai i paper
    papers = extract_papers_from_link(input_link)
    
    # Scarica i documenti HTML solo se validi
    download_count = 50
    for index, (title, html_link) in enumerate(papers, start=1):
        if is_valid_html(html_link):  # Verifica che il link sia HTML valido
            if download_html(title, html_link, index, output_dir):  # Scarica e controlla se è riuscito
                download_count += 1
                if download_count >= 300:  # Ferma i download al raggiungimento di 300 file
                    print("Raggiunto il limite di 300 documenti scaricati.")
                    break

    # Creazione del file di lettura
    with open('read_me.txt', 'w', encoding='utf-8') as readme_file:
        for index, (title, html_link) in enumerate(papers):
            # Scarica il paper e salva
            downloaded_file = download_html(title, html_link, index, output_dir)
            if downloaded_file:
                print(f'Scaricato: {downloaded_file}')
                readme_file.write(f"{title}: {html_link}\n")  # Salva il titolo e l'URL

if __name__ == "__main__":
    main()