package com.example.duan_appbanhang.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duan_appbanhang.Model.Address;
import com.example.duan_appbanhang.R;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {
    private List<Address> addressList;
    private int selectedPosition = 0; // Mặc định chọn vị trí đầu tiên
    private OnAddressItemClickListener listener;

    public AddressAdapter(List<Address> addressList) {
        this.addressList = addressList;
    }
    public interface OnAddressItemClickListener {
        void onAddressSelected(int position);
        void onEditClick(int position);
    }
    public void setOnAddressItemClickListener(OnAddressItemClickListener listener) {
        this.listener = listener;
    }
    @NonNull
    @Override
    public AddressAdapter.AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_item, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressAdapter.AddressViewHolder holder, int position) {
        Address address = addressList.get(position);

        // Định dạng thông tin địa chỉ theo yêu cầu
        String formattedAddress = address.getHoTen() + " | " + address.getSoDienThoai() + "\n" +
                address.getDiaChiChiTiet();

        holder.txtAddress.setText(formattedAddress);

        // Hiển thị hoặc ẩn nhãn mặc định
        if (address.isLaMacDinh()) {
            holder.tvDefault.setVisibility(View.VISIBLE);
        } else {
            holder.tvDefault.setVisibility(View.GONE);
        }

        // Thiết lập trạng thái cho RadioButton
        holder.radioAddress.setChecked(selectedPosition == position);

        // Xử lý sự kiện khi chọn địa chỉ
        holder.radioAddress.setOnClickListener(v -> {
            setSelectedPosition(holder.getAdapterPosition());
        });

        holder.itemView.setOnClickListener(v -> {
            setSelectedPosition(holder.getAdapterPosition());
        });

        // Xử lý nút Sửa
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(holder.getAdapterPosition());
            }
        });

    }
    public void setSelectedPosition(int position) {
        int previousSelected = selectedPosition;
        selectedPosition = position;

        // Cập nhật UI khi thay đổi lựa chọn
        notifyItemChanged(previousSelected);
        notifyItemChanged(selectedPosition);

        if (listener != null) {
            listener.onAddressSelected(selectedPosition);
        }
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public class AddressViewHolder extends RecyclerView.ViewHolder {
        RadioButton radioAddress;
        TextView txtAddress;
        TextView tvDefault;
        TextView btnEdit;
        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            radioAddress = itemView.findViewById(R.id.radioAddress);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            tvDefault = itemView.findViewById(R.id.tvDefault);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}
