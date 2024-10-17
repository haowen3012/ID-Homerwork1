import requests
from bs4 import BeautifulSoup
import os
import re
import time


"""Shows the first 200 papers on RAG (filter by title)"""
# input_link = "https://arxiv.org/search/?query=Retrieval+Augmented+generation&searchtype=title&abstracts=show&order=-announced_date_first&size=200"
"""Shows the first 200 papers on Data Fusion (filter by title)"""
input_link = "https://arxiv.org/search/?query=Data+fusion&searchtype=title&abstracts=show&order=-announced_date_first&size=200"
"""Shows the first 200 papers on Data Cleaning (filter by title)"""
# input_link="https://arxiv.org/search/?query=Data+Cleaning&searchtype=title&abstracts=show&order=-announced_date_first&size=200"


def clean_filename(filename):

    """Remove invalid characters for file names."""
    return re.sub(r'[\/:*?"<>|]', '', filename).strip()


def is_valid_html(url):

    """Check if the URL contains an HTML document."""
    try:
        response = requests.head(url)
        return 'text/html' in response.headers.get('Content-Type', '').lower()
    except requests.RequestException:
        return False


def download_html(title, url, index, output_dir):

    """Download the HTML content of a paper from a link and save it as a file."""
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


def extract_papers_from_link(link, max_results=200):

    """Extracts the associated paper links from the provided URL and returns their HTML links."""
    response = requests.get(link)
    response.raise_for_status()

    soup = BeautifulSoup(response.text, 'html.parser')
    paper_links = []

    # Find all paper links in the document
    for a in soup.find_all('a', href=True):
        href = a['href']
        if '/abs/' in href:

            # Replace /abs/ with /html/
            html_link = href.replace('/abs/', '/html/')
            paper_links.append((a.text.strip(), html_link))  # add the title and link

    return paper_links[:max_results]  #return only the first max_results


def main():
    current_dir = os.path.dirname(__file__)
    #sources_rag_path = os.path.join(current_dir, 'sources_RAG')     #cartella per RAG
    sources_rag_path = os.path.join(current_dir, 'sources_DF')       #cartella per DF
    #sources_rag_path = os.path.join(current_dir, 'sources_DC')      #cartella per DC

    # Directory where to save the HTML files
    output_dir = sources_rag_path
    os.makedirs(output_dir, exist_ok=True)
    
    # Extract papers from the input link ( first 200 and next 200)
    papers = extract_papers_from_link(input_link) + extract_papers_from_link(input_link + "&start=100")

    # Write the list of downloaded HTML documents to a file txt
    #with open('read_meRAG.txt', 'a', encoding='utf-8') as readme_file:      #per RAG
    with open('read_meDF.txt', 'a', encoding='utf-8') as readme_file:        #per DF
    #with open('read_meDC.txt', 'a', encoding='utf-8') as readme_file:       #per DC
        readme_file.write(f"Lista dei documenti HTML scaricati: \n\n")

    # Download the HTML content of the papers
    download_count = 0
    for index, (title, html_link) in enumerate(papers, start=1):
        if is_valid_html(html_link):  # check if the link is a valid HTML
            if download_html(title, html_link, index, output_dir):  # download and check if successful
                download_count += 1
                #with open('read_meRAG.txt', 'a', encoding='utf-8') as readme_file:      #per RAG
                with open('read_meDF.txt', 'a', encoding='utf-8') as readme_file:        #per DF
                #with open('read_meDC.txt', 'a', encoding='utf-8') as readme_file:       #per DC
                    print(f'downloaded: {html_link}')
                    readme_file.write(f"{index}. Titolo: {title} - Link: {html_link}\n")
                if download_count >= 300:  # stop downloads when 300 files are reached
                    print("300 papers limit reached.")
                    break
                time.sleep(2)  # Add a delay of 2 second between requests

if __name__ == "__main__":
    main()