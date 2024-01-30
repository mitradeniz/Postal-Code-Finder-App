import socket
import csv

class YourDataModel:
    def __init__(self, il, ilçe, semt_bucak_belde, Mahalle, PK):
        self.il = il
        self.ilçe = ilçe
        self.semt_bucak_belde = semt_bucak_belde
        self.Mahalle = Mahalle
        self.PK = PK

csv_file_path = "/Users/mitra/Downloads/app_data.csv"

with open(csv_file_path, newline='', encoding='utf-8') as csv_file:
    csv_reader = csv.reader(csv_file, delimiter=';')
    header = next(csv_reader)
    data_list = []

    for index, row in enumerate(csv_reader, start=1):
        cleaned_row = [cell.strip() for cell in row]

        try:
            data_model = YourDataModel(*cleaned_row)
            data_list.append(data_model)
        except IndexError:
            print(f"Hata: Satır {index} eksik sütun içeriyor: {row}")

def find_location(il, ilçe, semt_bucak_belde, Mahalle):
    matching_locations = []

    for data_model in data_list:
        if ilçe and Mahalle:
            if ilçe in data_model.ilçe and Mahalle in data_model.Mahalle:
                matching_locations.append({
                'il': data_model.il,
                'ilçe': data_model.ilçe,
                'semt_bucak_belde': data_model.semt_bucak_belde,
                'Mahalle': data_model.Mahalle,
                'PK': data_model.PK
            })


        elif (il and il in data_model.il) or (ilçe and ilçe in data_model.ilçe) or (semt_bucak_belde and semt_bucak_belde in data_model.semt_bucak_belde) or (Mahalle and Mahalle in data_model.Mahalle):
            matching_locations.append({
                'il': data_model.il,
                'ilçe': data_model.ilçe,
                'semt_bucak_belde': data_model.semt_bucak_belde,
                'Mahalle': data_model.Mahalle,
                'PK': data_model.PK
            })

    return matching_locations

server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket.bind(("192.168.1.35", 12345))
server_socket.listen(1)

print("Sunucu başlatıldı. İstemci bekleniyor...")

while True:
    client_socket, client_address = server_socket.accept()
    print(f"Istemci baglandi: {client_address}")

    data = client_socket.recv(4096)
    if not data:
        break

    data = data.decode()
    print(f"Sunucuya gelen veri: {data}")

    il, ilçe, semt_bucak_belde, Mahalle = data.split(',')
    matching_locations = find_location(il, ilçe, semt_bucak_belde, Mahalle)

    print(matching_locations)
    
    if len(matching_locations) == 5:
        response = f"Posta Kodu: {matching_locations} \b"
    elif matching_locations:
        response = "\n".join([f"{loc['il']} - {loc['ilçe']} - {loc['semt_bucak_belde']} - {loc['Mahalle']} - {loc['PK']}" for loc in matching_locations])
    else:
        response = f"{il} ili için eşleşen yer bulunamadı."

    client_socket.sendall(response.encode())
    

    client_socket.close()

server_socket.close()
