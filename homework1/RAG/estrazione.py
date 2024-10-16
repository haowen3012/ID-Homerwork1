import os
import json
from urllib.parse import urlparse

from lxml import etree
from bs4 import BeautifulSoup


def extract_tables_from_html_file(html_file, arxiv_id):
    # Leggi il contenuto del file HTML
    with open(html_file, 'r', encoding='utf-8') as file:
        content = file.read()

    # Parse HTML content with BeautifulSoup
    soup = BeautifulSoup(content, 'html.parser')

    # Use lxml to navigate with XPath
    parser = etree.HTMLParser()
    tree = etree.HTML(str(soup))

    # Extract all tables using XPath
    tables = tree.xpath('//table')

    # Initialize the data dictionary to store table data
    data = {}

    for i, table in enumerate(tables, start=1):
        # Extracting table caption (if available)

        table_id = table.xpath('./@id')[0]

        # Extract the caption of the table
        caption = table.xpath(f"./ancestor::figure/figcaption")


        caption_text = ''
        # Extract the text content of the caption
        if caption:
            caption_text = etree.tostring(caption[0], method='text', encoding='unicode').strip() if caption else ""


        # Extract the HTML of the table
        table_html = etree.tostring(table, pretty_print=True, method='html').decode('utf-8')


        # Extract footnotes related to the table (if available)
        footnotes = table.xpath(f".//cite")
        footnotes_array = []
        if footnotes:
            for index in  range(len(footnotes)):
                footnote_url = footnotes[index].xpath('./a/@href')[0]
                footnote_id = urlparse(footnote_url).fragment
                bibItem = tree.xpath(f"//li[@id='{footnote_id}']")[0]
                footnotes_array.append(etree.tostring(bibItem, method='text', encoding='unicode').strip().replace('&nbsp;', ' '))
        
        # Cerca i paragrafi che contengono un href con il frammento che fa riferimento alla tabella corrente
        references = tree.xpath(f"//p[a[contains(@href, '#{table_id}')]]")

        # Estrai il testo dei paragrafi trovati
        references_text = [
        etree.tostring(ref, method='text', encoding='unicode').strip() for ref in references
        ]
        
        # Store the extracted information in the dictionary
        data[table_id] = {
            "table": table_html,
            "caption": caption_text,
            "footnotes":footnotes_array,
            "references": references_text
        }

    current_dir = os.path.dirname(__file__)
    sources_rag_path = os.path.join(current_dir, 'extraction')
    # Define output directory
    #output_dir = r'C:\Users\h.zheng\Documents\Ingegneria dei Dati\ID-Homerworks\homework1\RAG\extraction'
    output_dir = r'ID-Homerworks\homework1\RAG\extraction'
    os.makedirs(output_dir, exist_ok=True)

    # Save the JSON data to a file named after the arxiv_id
    output_file = os.path.join(output_dir, f"{arxiv_id}.json")
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=4)

    print(f"Data extracted and saved to {output_file}")


def process_html_files_in_directory(directory):
    # Itera su tutti i file nella directory specificata
    for filename in os.listdir(directory):
        if filename.endswith(".html"):
            # Estrarre l'ID arXiv dal nome del file (es: "2409.17044.html" -> "2409.17044")
            arxiv_id = filename.split(".html")[0]
            html_file_path = os.path.join(directory, filename)
            extract_tables_from_html_file(html_file_path, arxiv_id)



def main():
    # Esempio di utilizzo
    #process_html_files_in_directory(r'C:\Users\h.zheng\Documents\Ingegneria dei Dati\ID-Homerworks\homework1\RAG\sources_RAG')
    process_html_files_in_directory(r'ID-Homerworks\homework1\RAG\sources_RAG')


if __name__ == "__main__":
    main()
