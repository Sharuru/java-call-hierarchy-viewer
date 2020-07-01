-- ----------------------------
-- Table structure for meta_table
-- ----------------------------
DROP TABLE IF EXISTS "public"."meta_table";
CREATE TABLE "public"."meta_table" (
  "id" int8 NOT NULL DEFAULT nextval('meta_table_id_seq'::regclass),
  "method_qualified_name" text COLLATE "pg_catalog"."default",
  "method_path" text COLLATE "pg_catalog"."default",
  "method_comment" text COLLATE "pg_catalog"."default",
  "method_type" text COLLATE "pg_catalog"."default",
  "method_callee_qualified_name" text COLLATE "pg_catalog"."default",
  "method_callee_seq" int8,
  "method_callee_class" text COLLATE "pg_catalog"."default",
  "method_callee_method" text COLLATE "pg_catalog"."default"
)
;

-- ----------------------------
-- Indexes structure for table meta_table
-- ----------------------------
CREATE INDEX "meta_table_method_callee_class_idx" ON "public"."meta_table" USING btree (
  "method_callee_class" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "meta_table_method_callee_method_idx" ON "public"."meta_table" USING btree (
  "method_callee_method" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "meta_table_method_callee_qualified_name_idx" ON "public"."meta_table" USING btree (
  "method_callee_qualified_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "meta_table_method_qualified_name_idx" ON "public"."meta_table" USING btree (
  "method_qualified_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table meta_table
-- ----------------------------
ALTER TABLE "public"."meta_table" ADD CONSTRAINT "meta_table_pkey" PRIMARY KEY ("id");
