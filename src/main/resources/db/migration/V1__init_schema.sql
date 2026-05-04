-- ცხრილი ოპტიმიზაციის რექვესთების ისტორიისთვის
CREATE TABLE optimization_requests (
                                       id UUID PRIMARY KEY,                   -- უნიკალური მოთხოვნის ID (მოთხოვნილია დავალებაში)
                                       max_volume INT NOT NULL,               -- ფურგონის მაქსიმალური ტევადობა
                                       total_volume INT NOT NULL,             -- შერჩეული ამანათების ჯამური მოცულობა
                                       total_revenue DECIMAL(10, 2) NOT NULL, -- ჯამური მოგება
                                       created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP -- შენახვის დრო
);

-- ცხრილი შერჩეული ამანათების დეტალებისთვის
CREATE TABLE selected_shipments (
                                    id SERIAL PRIMARY KEY,                 -- შიდა ავტომატური ID
                                    request_id UUID REFERENCES optimization_requests(id), -- კავშირი მთავარ რექვესთთან
                                    name VARCHAR(255) NOT NULL,            -- ამანათის სახელი
                                    volume INT NOT NULL,                   -- ამანათის მოცულობა
                                    revenue DECIMAL(10, 2) NOT NULL        -- ამანათის ფასი/მოგება
);