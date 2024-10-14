import os
import json
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
        caption = table.xpath('.//caption/text()')
        caption_text = caption[0].strip() if caption else "No caption available"

        # Extract the HTML of the table
        table_html = etree.tostring(table, pretty_print=True, method='html').decode('utf-8')

        # Extract footnotes related to the table (if available)
        footnotes = table.xpath('.//tfoot//text()')
        footnotes_text = [footnote.strip() for footnote in footnotes if footnote.strip()]
        
        # Find paragraphs that reference the table using 'table' keyword or similar
        references = tree.xpath(f"//p[contains(text(), 'Table {i}') or contains(text(), 'table {i}')]")
        references_text = [etree.tostring(ref, method='text', encoding='unicode').strip() for ref in references]

        # Store the extracted information in the dictionary
        data[f"id_table_{i}"] = {
            "caption": caption_text,
            "table": table_html,
            "footnotes": footnotes_text,
            "references": references_text
        }

    # Define output directory
    output_dir = "ID-Homerworks\homework1\Pose_Estimation\extraction"
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

# Esempio di utilizzo
process_html_files_in_directory("ID-Homerworks\homework1\Pose_Estimation\sources_PE")
