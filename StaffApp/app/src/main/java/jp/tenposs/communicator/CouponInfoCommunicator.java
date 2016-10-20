package jp.tenposs.communicator;

import android.os.Bundle;

import jp.tenposs.datamodel.CommonObject;
import jp.tenposs.datamodel.CommonResponse;
import jp.tenposs.datamodel.CouponInfo;
import jp.tenposs.datamodel.Key;

/**
 * Created by ambient on 10/17/16.
 */

public class CouponInfoCommunicator extends TenpossCommunicator {
    public CouponInfoCommunicator(TenpossCommunicatorListener listener) {
        super(listener);
    }

    @Override
    protected boolean request(Bundle bundle) {

        String strResponse = "{\n" +
                "code: \"1000\",\n" +
                "message: \"OK\",\n" +
                "data: {\n" +
                "coupons: [\n" +
                "{\n" +
                "id: 192,\n" +
                "type: null,\n" +
                "title: \"ggsgseg\",\n" +
                "description: \"gesgesrg\",\n" +
                "start_date: \"2016-09-15\",\n" +
                "end_date: \"2016-09-15\",\n" +
                "status: \"1\",\n" +
                "created_at: \"2016-09-15 08:48:40\",\n" +
                "updated_at: \"2016-09-15 08:48:40\",\n" +
                "deleted_at: null,\n" +
                "image_url: \"https://ten-po.com/uploads/5f5514cfe0abaefcb549ddd7882e00c4.png\",\n" +
                "limit: null,\n" +
                "coupon_type_id: \"1\",\n" +
                "taglist: [\n" +
                "\"messi\"\n" +
                "],\n" +
                "can_use: false,\n" +
                "code: \"\",\n" +
                "coupon_type: {\n" +
                "id: 1,\n" +
                "name: \"cp1\",\n" +
                "store_id: \"1\",\n" +
                "deleted_at: null\n" +
                "}\n" +
                "},\n" +
                "{\n" +
                "id: 191,\n" +
                "type: null,\n" +
                "title: \"fwafwefawef\",\n" +
                "description: \"fweafwfwef\",\n" +
                "start_date: \"2016-09-15\",\n" +
                "end_date: \"2016-09-16\",\n" +
                "status: \"1\",\n" +
                "created_at: \"2016-09-15 07:31:21\",\n" +
                "updated_at: \"2016-09-15 07:31:21\",\n" +
                "deleted_at: null,\n" +
                "image_url: \"https://ten-po.com/uploads/dabce1b95881f93b86fe6ab156e60f36.PNG\",\n" +
                "limit: null,\n" +
                "coupon_type_id: \"1\",\n" +
                "taglist: [\n" +
                "\"vietnam\"\n" +
                "],\n" +
                "can_use: false,\n" +
                "code: \"\",\n" +
                "coupon_type: {\n" +
                "id: 1,\n" +
                "name: \"cp1\",\n" +
                "store_id: \"1\",\n" +
                "deleted_at: null\n" +
                "}\n" +
                "},\n" +
                "{\n" +
                "id: 190,\n" +
                "type: null,\n" +
                "title: \"fwfawe\",\n" +
                "description: \"fawefwef\",\n" +
                "start_date: \"2016-09-15\",\n" +
                "end_date: \"2016-09-15\",\n" +
                "status: \"1\",\n" +
                "created_at: \"2016-09-15 07:16:06\",\n" +
                "updated_at: \"2016-09-15 07:16:06\",\n" +
                "deleted_at: null,\n" +
                "image_url: \"https://ten-po.com/uploads/5cf6846dddf6837238e68f541df124ef.png\",\n" +
                "limit: null,\n" +
                "coupon_type_id: \"1\",\n" +
                "taglist: [\n" +
                "\"halongbay\"\n" +
                "],\n" +
                "can_use: false,\n" +
                "code: \"\",\n" +
                "coupon_type: {\n" +
                "id: 1,\n" +
                "name: \"cp1\",\n" +
                "store_id: \"1\",\n" +
                "deleted_at: null\n" +
                "}\n" +
                "},\n" +
                "{\n" +
                "id: 189,\n" +
                "type: null,\n" +
                "title: \"fawfwefwfwef\",\n" +
                "description: \"Tiếp ông Mã Giang Kiểm, Tổng Giám đốc Công ty Cục 6 Đường sắt Trung Quốc, Thủ tướng nói ngay đến việc thi công đường sắt trên cao Cát Linh - Hà Đông, một dự án do công ty này làm tổng thầu EPC. Cho rằng việc chậm tiến độ của dự án có nhiều nguyên nhân, nhưng Thủ tướng nhấn mạnh, phải thẳng thắn nhìn nhận về những nguyên nhân chủ quan. “Dù nguyên nhân gì thì với vai trò tổng thầu gây chậm trễ cũng cần phải nhìn nhận trách nhiệm, nhất là trong khi nhiều doanh nghiệp tư nhân khác họ làm rất thành công”, Thủ tướng nói. Người đứng đầu Chính phủ cũng bày tỏ lo ngại trước tình trạng “công trường vắng vẻ” và cho rằng, “tổ chức thi công như thế thì gay go lắm”. Thủ tướng yêu cầu Công ty Cục 6 phải khắc phục các bất cập, tồn tại, đẩy nhanh tiến độ thi công dự án, đồng thời bảo đảm chất lượng công trình, an toàn lao động khi thi công. Đồng thời, Thủ tướng lưu ý, tổng thầu Trung Quốc phải chọn được các nhà thầu bảo đảm năng lực, nếu không thì thay thế nhà thầu. Thủ tướng cũng đề nghị Công ty Cục 6 chủ động làm việc cụ thể với các cơ quan chức năng của Việt Nam để xử lý các vấn đề nảy sinh trong thực hiện dự án, nhất là khi hai bên đã ký Hiệp định khung về khoản vay bổ sung 250,62 triệu USD cho tuyến đường sắt đô thị Cát Linh - Hà Đông sau cuộc hội đàm giữa Thủ tướng hai nước ngày hôm qua, 12/9. Tiếp thu ý kiến của Thủ tướng, ông Mã Giang Kiểm cam kết sẽ nỗ lực đẩy nhanh tiến độ thi công dự án. “Chúng tôi đã chốt lại tiến độ dự án. Cuối tháng 12/2016, sẽ hoàn thành xây dựng cơ bản”, lãnh đạo Công ty Cục 6 báo cáo. Đồng thời ông này khẳng định, tháng 1/2017, dự án sẽ hoàn thành khu Depot; tháng 7/2017 tiến hành chạy thử, sau đó, đến tháng 9/2017 bắt đầu khai thác thử. Nhân dịp này, lãnh đạo Công ty Cục 6 cũng nêu một số kiến nghị mong các cơ quan chức năng Việt Nam quan tâm giải quyết để đẩy nhanh tiến độ dự án này. Cũng trong sáng nay, Thủ tướng đã tiếp ông Triệu Hồng Tĩnh, Phó Tổng Giám đốc điều hành Công ty TNHH cổ phần Hoa Hạ Hạnh Phúc, cũng là một doanh nghiệp hoạt động trong lĩnh vực cơ sở hạ tầng. Đây là nhà cung cấp các giải pháp toàn diện về phát triển kinh tế - xã hội theo hình thức PPP với các thành phố Trung Quốc, trong đó có lĩnh vực phát triển cơ sở hạ tầng, khu công nghiệp, đường sắt cao tốc. Hoan nghênh sự hợp tác giữa công ty với các địa phương của Việt Nam, Thủ tướng mong muốn công ty sẽ đầu tư hiệu quả, có bước tiến mới trong hoạt động tại Việt Nam. “Chúng tôi tin tưởng vào tiềm năng, triển vọng phát triển của Việt Nam. Hằng tháng tôi đều đến thăm Việt Nam vài ngày”, ông Triệu Hồng Tĩnh nói và bày tỏ mong muốn đẩy mạnh đầu tư vào thị trường Việt Nam.\",\n" +
                "start_date: \"2016-09-08\",\n" +
                "end_date: \"2016-09-08\",\n" +
                "status: \"1\",\n" +
                "created_at: \"2016-09-08 08:35:34\",\n" +
                "updated_at: \"2016-09-13 11:41:56\",\n" +
                "deleted_at: null,\n" +
                "image_url: \"https://scontent-hkg3-1.cdninstagram.com/t51.2885-15/sh0.08/e35/p640x640/14350725_1681866755466544_760470209_n.jpg?ig_cache_key=MTMzNzkyNjM1MDk1ODU0ODU2OA%3D%3D.2\",\n" +
                "limit: null,\n" +
                "coupon_type_id: \"1\",\n" +
                "taglist: [\n" +
                "\"messi\"\n" +
                "],\n" +
                "can_use: false,\n" +
                "code: \"\",\n" +
                "coupon_type: {\n" +
                "id: 1,\n" +
                "name: \"cp1\",\n" +
                "store_id: \"1\",\n" +
                "deleted_at: null\n" +
                "}\n" +
                "},\n" +
                "{\n" +
                "id: 188,\n" +
                "type: null,\n" +
                "title: \"afwefawef\",\n" +
                "description: \"fawfawefew\",\n" +
                "start_date: \"2016-09-08\",\n" +
                "end_date: \"2016-09-08\",\n" +
                "status: \"1\",\n" +
                "created_at: \"2016-09-08 08:34:51\",\n" +
                "updated_at: \"2016-09-08 08:34:51\",\n" +
                "deleted_at: null,\n" +
                "image_url: \"https://scontent-hkg3-1.cdninstagram.com/t51.2885-15/sh0.08/e35/p640x640/14350725_1681866755466544_760470209_n.jpg?ig_cache_key=MTMzNzkyNjM1MDk1ODU0ODU2OA%3D%3D.2\",\n" +
                "limit: null,\n" +
                "coupon_type_id: \"1\",\n" +
                "taglist: [\n" +
                "\"hanoi\"\n" +
                "],\n" +
                "can_use: false,\n" +
                "code: \"\",\n" +
                "coupon_type: {\n" +
                "id: 1,\n" +
                "name: \"cp1\",\n" +
                "store_id: \"1\",\n" +
                "deleted_at: null\n" +
                "}\n" +
                "},\n" +
                "{\n" +
                "id: 187,\n" +
                "type: null,\n" +
                "title: \"fwefewf\",\n" +
                "description: \"afwefew\",\n" +
                "start_date: \"2016-09-08\",\n" +
                "end_date: \"2016-09-08\",\n" +
                "status: \"1\",\n" +
                "created_at: \"2016-09-08 08:34:14\",\n" +
                "updated_at: \"2016-09-08 08:34:14\",\n" +
                "deleted_at: null,\n" +
                "image_url: \"https://scontent-hkg3-1.cdninstagram.com/t51.2885-15/sh0.08/e35/p640x640/14350725_1681866755466544_760470209_n.jpg?ig_cache_key=MTMzNzkyNjM1MDk1ODU0ODU2OA%3D%3D.2\",\n" +
                "limit: null,\n" +
                "coupon_type_id: \"1\",\n" +
                "taglist: [\n" +
                "\"vietnam\"\n" +
                "],\n" +
                "can_use: false,\n" +
                "code: \"\",\n" +
                "coupon_type: {\n" +
                "id: 1,\n" +
                "name: \"cp1\",\n" +
                "store_id: \"1\",\n" +
                "deleted_at: null\n" +
                "}\n" +
                "},\n" +
                "{\n" +
                "id: 186,\n" +
                "type: null,\n" +
                "title: \"fwefewf\",\n" +
                "description: \"afwefew\",\n" +
                "start_date: \"2016-09-08\",\n" +
                "end_date: \"2016-09-08\",\n" +
                "status: \"1\",\n" +
                "created_at: \"2016-09-08 08:33:34\",\n" +
                "updated_at: \"2016-09-08 08:33:34\",\n" +
                "deleted_at: null,\n" +
                "image_url: \"https://scontent-hkg3-1.cdninstagram.com/t51.2885-15/sh0.08/e35/p640x640/14350725_1681866755466544_760470209_n.jpg?ig_cache_key=MTMzNzkyNjM1MDk1ODU0ODU2OA%3D%3D.2\",\n" +
                "limit: null,\n" +
                "coupon_type_id: \"1\",\n" +
                "taglist: [\n" +
                "\"vietnam\"\n" +
                "],\n" +
                "can_use: false,\n" +
                "code: \"\",\n" +
                "coupon_type: {\n" +
                "id: 1,\n" +
                "name: \"cp1\",\n" +
                "store_id: \"1\",\n" +
                "deleted_at: null\n" +
                "}\n" +
                "},\n" +
                "{\n" +
                "id: 185,\n" +
                "type: null,\n" +
                "title: \"fwefewf\",\n" +
                "description: \"afwefew\",\n" +
                "start_date: \"2016-09-08\",\n" +
                "end_date: \"2016-09-08\",\n" +
                "status: \"1\",\n" +
                "created_at: \"2016-09-08 08:31:51\",\n" +
                "updated_at: \"2016-09-08 08:31:51\",\n" +
                "deleted_at: null,\n" +
                "image_url: \"https://scontent-hkg3-1.cdninstagram.com/t51.2885-15/sh0.08/e35/p640x640/14350725_1681866755466544_760470209_n.jpg?ig_cache_key=MTMzNzkyNjM1MDk1ODU0ODU2OA%3D%3D.2\",\n" +
                "limit: null,\n" +
                "coupon_type_id: \"1\",\n" +
                "taglist: [\n" +
                "\"vietnam\"\n" +
                "],\n" +
                "can_use: false,\n" +
                "code: \"\",\n" +
                "coupon_type: {\n" +
                "id: 1,\n" +
                "name: \"cp1\",\n" +
                "store_id: \"1\",\n" +
                "deleted_at: null\n" +
                "}\n" +
                "},\n" +
                "{\n" +
                "id: 184,\n" +
                "type: null,\n" +
                "title: \"fwefewf\",\n" +
                "description: \"afwefew\",\n" +
                "start_date: \"2016-09-08\",\n" +
                "end_date: \"2016-09-08\",\n" +
                "status: \"1\",\n" +
                "created_at: \"2016-09-08 08:30:11\",\n" +
                "updated_at: \"2016-09-08 08:30:11\",\n" +
                "deleted_at: null,\n" +
                "image_url: \"https://scontent-hkg3-1.cdninstagram.com/t51.2885-15/sh0.08/e35/p640x640/14350725_1681866755466544_760470209_n.jpg?ig_cache_key=MTMzNzkyNjM1MDk1ODU0ODU2OA%3D%3D.2\",\n" +
                "limit: null,\n" +
                "coupon_type_id: \"1\",\n" +
                "taglist: [\n" +
                "\"vietnam\"\n" +
                "],\n" +
                "can_use: false,\n" +
                "code: \"\",\n" +
                "coupon_type: {\n" +
                "id: 1,\n" +
                "name: \"cp1\",\n" +
                "store_id: \"1\",\n" +
                "deleted_at: null\n" +
                "}\n" +
                "},\n" +
                "{\n" +
                "id: 183,\n" +
                "type: null,\n" +
                "title: \"fwefewf\",\n" +
                "description: \"afwefew\",\n" +
                "start_date: \"2016-09-08\",\n" +
                "end_date: \"2016-09-08\",\n" +
                "status: \"1\",\n" +
                "created_at: \"2016-09-08 08:29:29\",\n" +
                "updated_at: \"2016-09-08 08:29:29\",\n" +
                "deleted_at: null,\n" +
                "image_url: \"https://scontent-hkg3-1.cdninstagram.com/t51.2885-15/sh0.08/e35/p640x640/14350725_1681866755466544_760470209_n.jpg?ig_cache_key=MTMzNzkyNjM1MDk1ODU0ODU2OA%3D%3D.2\",\n" +
                "limit: null,\n" +
                "coupon_type_id: \"1\",\n" +
                "taglist: [\n" +
                "\"vietnam\"\n" +
                "],\n" +
                "can_use: false,\n" +
                "code: \"\",\n" +
                "coupon_type: {\n" +
                "id: 1,\n" +
                "name: \"cp1\",\n" +
                "store_id: \"1\",\n" +
                "deleted_at: null\n" +
                "}\n" +
                "},\n" +
                "{\n" +
                "id: 182,\n" +
                "type: null,\n" +
                "title: \"fwefewf\",\n" +
                "description: \"afwefew\",\n" +
                "start_date: \"2016-09-08\",\n" +
                "end_date: \"2016-09-08\",\n" +
                "status: \"1\",\n" +
                "created_at: \"2016-09-08 08:28:37\",\n" +
                "updated_at: \"2016-09-08 08:28:37\",\n" +
                "deleted_at: null,\n" +
                "image_url: \"https://scontent-hkg3-1.cdninstagram.com/t51.2885-15/sh0.08/e35/p640x640/14350725_1681866755466544_760470209_n.jpg?ig_cache_key=MTMzNzkyNjM1MDk1ODU0ODU2OA%3D%3D.2\",\n" +
                "limit: null,\n" +
                "coupon_type_id: \"1\",\n" +
                "taglist: [\n" +
                "\"vietnam\"\n" +
                "],\n" +
                "can_use: false,\n" +
                "code: \"\",\n" +
                "coupon_type: {\n" +
                "id: 1,\n" +
                "name: \"cp1\",\n" +
                "store_id: \"1\",\n" +
                "deleted_at: null\n" +
                "}\n" +
                "},\n" +
                "{\n" +
                "id: 181,\n" +
                "type: null,\n" +
                "title: \"fwefewf\",\n" +
                "description: \"afwefew\",\n" +
                "start_date: \"2016-09-08\",\n" +
                "end_date: \"2016-09-08\",\n" +
                "status: \"1\",\n" +
                "created_at: \"2016-09-08 08:28:30\",\n" +
                "updated_at: \"2016-09-08 08:28:30\",\n" +
                "deleted_at: null,\n" +
                "image_url: \"https://scontent-hkg3-1.cdninstagram.com/t51.2885-15/sh0.08/e35/p640x640/14350725_1681866755466544_760470209_n.jpg?ig_cache_key=MTMzNzkyNjM1MDk1ODU0ODU2OA%3D%3D.2\",\n" +
                "limit: null,\n" +
                "coupon_type_id: \"1\",\n" +
                "taglist: [\n" +
                "\"vietnam\"\n" +
                "],\n" +
                "can_use: false,\n" +
                "code: \"\",\n" +
                "coupon_type: {\n" +
                "id: 1,\n" +
                "name: \"cp1\",\n" +
                "store_id: \"1\",\n" +
                "deleted_at: null\n" +
                "}\n" +
                "},\n" +
                "{\n" +
                "id: 180,\n" +
                "type: null,\n" +
                "title: \"fwefewf\",\n" +
                "description: \"afwefew\",\n" +
                "start_date: \"2016-09-08\",\n" +
                "end_date: \"2016-09-08\",\n" +
                "status: \"1\",\n" +
                "created_at: \"2016-09-08 08:19:02\",\n" +
                "updated_at: \"2016-09-08 08:19:02\",\n" +
                "deleted_at: null,\n" +
                "image_url: \"https://scontent-hkg3-1.cdninstagram.com/t51.2885-15/sh0.08/e35/p640x640/14350725_1681866755466544_760470209_n.jpg?ig_cache_key=MTMzNzkyNjM1MDk1ODU0ODU2OA%3D%3D.2\",\n" +
                "limit: null,\n" +
                "coupon_type_id: \"1\",\n" +
                "taglist: [\n" +
                "\"vietnam\"\n" +
                "],\n" +
                "can_use: false,\n" +
                "code: \"\",\n" +
                "coupon_type: {\n" +
                "id: 1,\n" +
                "name: \"cp1\",\n" +
                "store_id: \"1\",\n" +
                "deleted_at: null\n" +
                "}\n" +
                "},\n" +
                "{\n" +
                "id: 179,\n" +
                "type: null,\n" +
                "title: \"fwefweff\",\n" +
                "description: \"wefwef\",\n" +
                "start_date: \"2016-09-08\",\n" +
                "end_date: \"2016-09-08\",\n" +
                "status: \"1\",\n" +
                "created_at: \"2016-09-08 08:16:21\",\n" +
                "updated_at: \"2016-09-08 08:16:21\",\n" +
                "deleted_at: null,\n" +
                "image_url: \"https://scontent-hkg3-1.cdninstagram.com/t51.2885-15/sh0.08/e35/p640x640/14350725_1681866755466544_760470209_n.jpg?ig_cache_key=MTMzNzkyNjM1MDk1ODU0ODU2OA%3D%3D.2\",\n" +
                "limit: null,\n" +
                "coupon_type_id: \"1\",\n" +
                "taglist: [\n" +
                "\"vietnam\"\n" +
                "],\n" +
                "can_use: false,\n" +
                "code: \"\",\n" +
                "coupon_type: {\n" +
                "id: 1,\n" +
                "name: \"cp1\",\n" +
                "store_id: \"1\",\n" +
                "deleted_at: null\n" +
                "}\n" +
                "},\n" +
                "{\n" +
                "id: 178,\n" +
                "type: null,\n" +
                "title: \"faweff\",\n" +
                "description: \"fwaefewa\",\n" +
                "start_date: \"2016-09-08\",\n" +
                "end_date: \"2016-09-08\",\n" +
                "status: \"1\",\n" +
                "created_at: \"2016-09-08 08:12:42\",\n" +
                "updated_at: \"2016-09-08 08:12:42\",\n" +
                "deleted_at: null,\n" +
                "image_url: \"https://scontent-hkg3-1.cdninstagram.com/t51.2885-15/sh0.08/e35/p640x640/14350725_1681866755466544_760470209_n.jpg?ig_cache_key=MTMzNzkyNjM1MDk1ODU0ODU2OA%3D%3D.2\",\n" +
                "limit: null,\n" +
                "coupon_type_id: \"1\",\n" +
                "taglist: [\n" +
                "\"halongbay\"\n" +
                "],\n" +
                "can_use: false,\n" +
                "code: \"\",\n" +
                "coupon_type: {\n" +
                "id: 1,\n" +
                "name: \"cp1\",\n" +
                "store_id: \"1\",\n" +
                "deleted_at: null\n" +
                "}\n" +
                "},\n" +
                "{\n" +
                "id: 177,\n" +
                "type: null,\n" +
                "title: \"afwefwefawe\",\n" +
                "description: \"fawfwefawfe\",\n" +
                "start_date: \"2016-09-08\",\n" +
                "end_date: \"2016-09-08\",\n" +
                "status: \"1\",\n" +
                "created_at: \"2016-09-08 08:08:58\",\n" +
                "updated_at: \"2016-09-08 08:08:58\",\n" +
                "deleted_at: null,\n" +
                "image_url: \"https://scontent-hkg3-1.cdninstagram.com/t51.2885-15/sh0.08/e35/p640x640/14350725_1681866755466544_760470209_n.jpg?ig_cache_key=MTMzNzkyNjM1MDk1ODU0ODU2OA%3D%3D.2\",\n" +
                "limit: null,\n" +
                "coupon_type_id: \"1\",\n" +
                "taglist: [\n" +
                "\"halongbay\"\n" +
                "],\n" +
                "can_use: false,\n" +
                "code: \"\",\n" +
                "coupon_type: {\n" +
                "id: 1,\n" +
                "name: \"cp1\",\n" +
                "store_id: \"1\",\n" +
                "deleted_at: null\n" +
                "}\n" +
                "},\n" +
                "{\n" +
                "id: 176,\n" +
                "type: null,\n" +
                "title: \"afwefwefawe\",\n" +
                "description: \"fawfwefawfe\",\n" +
                "start_date: \"2016-09-08\",\n" +
                "end_date: \"2016-09-08\",\n" +
                "status: \"1\",\n" +
                "created_at: \"2016-09-08 08:03:05\",\n" +
                "updated_at: \"2016-09-08 08:03:05\",\n" +
                "deleted_at: null,\n" +
                "image_url: \"https://scontent-hkg3-1.cdninstagram.com/t51.2885-15/sh0.08/e35/p640x640/14350725_1681866755466544_760470209_n.jpg?ig_cache_key=MTMzNzkyNjM1MDk1ODU0ODU2OA%3D%3D.2\",\n" +
                "limit: null,\n" +
                "coupon_type_id: \"1\",\n" +
                "taglist: [\n" +
                "\"halongbay\"\n" +
                "],\n" +
                "can_use: false,\n" +
                "code: \"\",\n" +
                "coupon_type: {\n" +
                "id: 1,\n" +
                "name: \"cp1\",\n" +
                "store_id: \"1\",\n" +
                "deleted_at: null\n" +
                "}\n" +
                "},\n" +
                "{\n" +
                "id: 175,\n" +
                "type: null,\n" +
                "title: \"21212\",\n" +
                "description: \"12121212\",\n" +
                "start_date: \"2016-08-31\",\n" +
                "end_date: \"2016-09-14\",\n" +
                "status: \"1\",\n" +
                "created_at: \"2016-08-31 04:14:25\",\n" +
                "updated_at: \"2016-08-31 04:14:25\",\n" +
                "deleted_at: null,\n" +
                "image_url: \"https://scontent-hkg3-1.cdninstagram.com/t51.2885-15/sh0.08/e35/p640x640/14350725_1681866755466544_760470209_n.jpg?ig_cache_key=MTMzNzkyNjM1MDk1ODU0ODU2OA%3D%3D.2\",\n" +
                "limit: null,\n" +
                "coupon_type_id: \"1\",\n" +
                "taglist: [\n" +
                "\"sea\"\n" +
                "],\n" +
                "can_use: false,\n" +
                "code: \"\",\n" +
                "coupon_type: {\n" +
                "id: 1,\n" +
                "name: \"cp1\",\n" +
                "store_id: \"1\",\n" +
                "deleted_at: null\n" +
                "}\n" +
                "},\n" +
                "{\n" +
                "id: 165,\n" +
                "type: null,\n" +
                "title: \"eqeq\",\n" +
                "description: \"wqewqe\",\n" +
                "start_date: \"2016-08-24\",\n" +
                "end_date: \"2016-08-24\",\n" +
                "status: \"1\",\n" +
                "created_at: \"2016-08-24 06:14:04\",\n" +
                "updated_at: \"2016-08-24 06:14:04\",\n" +
                "deleted_at: null,\n" +
                "image_url: \"https://scontent-hkg3-1.cdninstagram.com/t51.2885-15/sh0.08/e35/p640x640/14350725_1681866755466544_760470209_n.jpg?ig_cache_key=MTMzNzkyNjM1MDk1ODU0ODU2OA%3D%3D.2\",\n" +
                "limit: null,\n" +
                "coupon_type_id: \"1\",\n" +
                "taglist: [\n" +
                "\"halongbay\"\n" +
                "],\n" +
                "can_use: false,\n" +
                "code: \"\",\n" +
                "coupon_type: {\n" +
                "id: 1,\n" +
                "name: \"cp1\",\n" +
                "store_id: \"1\",\n" +
                "deleted_at: null\n" +
                "}\n" +
                "},\n" +
                "{\n" +
                "id: 164,\n" +
                "type: null,\n" +
                "title: \"eqeq\",\n" +
                "description: \"wqewqe\",\n" +
                "start_date: \"2016-08-24\",\n" +
                "end_date: \"2016-08-24\",\n" +
                "status: \"1\",\n" +
                "created_at: \"2016-08-24 06:13:46\",\n" +
                "updated_at: \"2016-08-24 06:13:46\",\n" +
                "deleted_at: null,\n" +
                "image_url: \"https://scontent-hkg3-1.cdninstagram.com/t51.2885-15/sh0.08/e35/p640x640/14350725_1681866755466544_760470209_n.jpg?ig_cache_key=MTMzNzkyNjM1MDk1ODU0ODU2OA%3D%3D.2\",\n" +
                "limit: null,\n" +
                "coupon_type_id: \"1\",\n" +
                "taglist: [\n" +
                "\"halongbay\"\n" +
                "],\n" +
                "can_use: false,\n" +
                "code: \"\",\n" +
                "coupon_type: {\n" +
                "id: 1,\n" +
                "name: \"cp1\",\n" +
                "store_id: \"1\",\n" +
                "deleted_at: null\n" +
                "}\n" +
                "},\n" +
                "{\n" +
                "id: 163,\n" +
                "type: null,\n" +
                "title: \"eqeq\",\n" +
                "description: \"wqewqe\",\n" +
                "start_date: \"2016-08-24\",\n" +
                "end_date: \"2016-08-24\",\n" +
                "status: \"1\",\n" +
                "created_at: \"2016-08-24 06:13:11\",\n" +
                "updated_at: \"2016-08-24 06:13:11\",\n" +
                "deleted_at: null,\n" +
                "image_url: \"https://scontent-hkg3-1.cdninstagram.com/t51.2885-15/sh0.08/e35/p640x640/14350725_1681866755466544_760470209_n.jpg?ig_cache_key=MTMzNzkyNjM1MDk1ODU0ODU2OA%3D%3D.2\",\n" +
                "limit: null,\n" +
                "coupon_type_id: \"1\",\n" +
                "taglist: [\n" +
                "\"halongbay\"\n" +
                "],\n" +
                "can_use: false,\n" +
                "code: \"\",\n" +
                "coupon_type: {\n" +
                "id: 1,\n" +
                "name: \"cp1\",\n" +
                "store_id: \"1\",\n" +
                "deleted_at: null\n" +
                "}\n" +
                "},\n" +
                "{\n" +
                "id: 162,\n" +
                "type: null,\n" +
                "title: \"eqeq\",\n" +
                "description: \"wqewqe\",\n" +
                "start_date: \"2016-08-24\",\n" +
                "end_date: \"2016-08-24\",\n" +
                "status: \"1\",\n" +
                "created_at: \"2016-08-24 06:11:11\",\n" +
                "updated_at: \"2016-08-24 06:11:11\",\n" +
                "deleted_at: null,\n" +
                "image_url: \"https://scontent-hkg3-1.cdninstagram.com/t51.2885-15/sh0.08/e35/p640x640/14350725_1681866755466544_760470209_n.jpg?ig_cache_key=MTMzNzkyNjM1MDk1ODU0ODU2OA%3D%3D.2\",\n" +
                "limit: null,\n" +
                "coupon_type_id: \"1\",\n" +
                "taglist: [\n" +
                "\"halongbay\"\n" +
                "],\n" +
                "can_use: false,\n" +
                "code: \"\",\n" +
                "coupon_type: {\n" +
                "id: 1,\n" +
                "name: \"cp1\",\n" +
                "store_id: \"1\",\n" +
                "deleted_at: null\n" +
                "}\n" +
                "},\n" +
                "{\n" +
                "id: 161,\n" +
                "type: null,\n" +
                "title: \"eqeq\",\n" +
                "description: \"wqewqe\",\n" +
                "start_date: \"2016-08-24\",\n" +
                "end_date: \"2016-08-24\",\n" +
                "status: \"1\",\n" +
                "created_at: \"2016-08-24 06:10:28\",\n" +
                "updated_at: \"2016-08-24 06:10:28\",\n" +
                "deleted_at: null,\n" +
                "image_url: \"https://scontent-hkg3-1.cdninstagram.com/t51.2885-15/sh0.08/e35/p640x640/14350725_1681866755466544_760470209_n.jpg?ig_cache_key=MTMzNzkyNjM1MDk1ODU0ODU2OA%3D%3D.2\",\n" +
                "limit: null,\n" +
                "coupon_type_id: \"1\",\n" +
                "taglist: [\n" +
                "\"halongbay\"\n" +
                "],\n" +
                "can_use: false,\n" +
                "code: \"\",\n" +
                "coupon_type: {\n" +
                "id: 1,\n" +
                "name: \"cp1\",\n" +
                "store_id: \"1\",\n" +
                "deleted_at: null\n" +
                "}\n" +
                "},\n" +
                "{\n" +
                "id: 160,\n" +
                "type: null,\n" +
                "title: \"eqeq\",\n" +
                "description: \"wqewqe\",\n" +
                "start_date: \"2016-08-24\",\n" +
                "end_date: \"2016-08-24\",\n" +
                "status: \"1\",\n" +
                "created_at: \"2016-08-24 06:10:05\",\n" +
                "updated_at: \"2016-08-24 06:10:05\",\n" +
                "deleted_at: null,\n" +
                "image_url: \"https://scontent-hkg3-1.cdninstagram.com/t51.2885-15/sh0.08/e35/p640x640/14350725_1681866755466544_760470209_n.jpg?ig_cache_key=MTMzNzkyNjM1MDk1ODU0ODU2OA%3D%3D.2\",\n" +
                "limit: null,\n" +
                "coupon_type_id: \"1\",\n" +
                "taglist: [\n" +
                "\"halongbay\"\n" +
                "],\n" +
                "can_use: false,\n" +
                "code: \"\",\n" +
                "coupon_type: {\n" +
                "id: 1,\n" +
                "name: \"cp1\",\n" +
                "store_id: \"1\",\n" +
                "deleted_at: null\n" +
                "}\n" +
                "},\n" +
                "{\n" +
                "id: 159,\n" +
                "type: null,\n" +
                "title: \"eqeq\",\n" +
                "description: \"wqewqe\",\n" +
                "start_date: \"2016-08-24\",\n" +
                "end_date: \"2016-08-24\",\n" +
                "status: \"1\",\n" +
                "created_at: \"2016-08-24 06:08:55\",\n" +
                "updated_at: \"2016-08-24 06:08:55\",\n" +
                "deleted_at: null,\n" +
                "image_url: \"https://scontent-hkg3-1.cdninstagram.com/t51.2885-15/sh0.08/e35/p640x640/14350725_1681866755466544_760470209_n.jpg?ig_cache_key=MTMzNzkyNjM1MDk1ODU0ODU2OA%3D%3D.2\",\n" +
                "limit: null,\n" +
                "coupon_type_id: \"1\",\n" +
                "taglist: [\n" +
                "\"halongbay\"\n" +
                "],\n" +
                "can_use: false,\n" +
                "code: \"\",\n" +
                "coupon_type: {\n" +
                "id: 1,\n" +
                "name: \"cp1\",\n" +
                "store_id: \"1\",\n" +
                "deleted_at: null\n" +
                "}\n" +
                "}\n" +
                "],\n" +
                "total_coupons: 25\n" +
                "}\n" +
                "}";
        CommonResponse response = (CouponInfo.Response) CommonObject.fromJSONString(strResponse, CouponInfo.Response.class, null);
        if (response == null) {
            response = (CommonResponse) CommonObject.fromJSONString(strResponse, CommonResponse.class, null);
        }
        if (response != null) {
            bundle.putInt(Key.ResponseResult, TenpossCommunicator.CommunicationCode.ConnectionSuccess.ordinal());
            bundle.putInt(Key.ResponseResultApi, response.code);

            if (response.code == CommonResponse.ResultSuccess) {
                bundle.putSerializable(Key.ResponseObject, response);
            } else {
                bundle.putString(Key.ResponseMessage, response.message);
            }
        } else {
            bundle.putInt(Key.ResponseResult, TenpossCommunicator.CommunicationCode.ConnectionFailed.ordinal());
            bundle.putString(Key.ResponseMessage, "Invalid response data!");
        }
        return false;
    }
}
