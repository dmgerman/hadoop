begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.select
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|select
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  * Options related to S3 Select.  *  * These options are set for the entire filesystem unless overridden  * as an option in the URI  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|SelectConstants
specifier|public
specifier|final
class|class
name|SelectConstants
block|{
DECL|field|SELECT_UNSUPPORTED
specifier|public
specifier|static
specifier|final
name|String
name|SELECT_UNSUPPORTED
init|=
literal|"S3 Select is not supported"
decl_stmt|;
DECL|method|SelectConstants ()
specifier|private
name|SelectConstants
parameter_list|()
block|{   }
DECL|field|FS_S3A_SELECT
specifier|public
specifier|static
specifier|final
name|String
name|FS_S3A_SELECT
init|=
literal|"fs.s3a.select."
decl_stmt|;
comment|/**    * This is the big SQL expression: {@value}.    * When used in an open() call, switch to a select operation.    * This is only used in the open call, never in a filesystem configuration.    */
DECL|field|SELECT_SQL
specifier|public
specifier|static
specifier|final
name|String
name|SELECT_SQL
init|=
name|FS_S3A_SELECT
operator|+
literal|"sql"
decl_stmt|;
comment|/**    * Does the FS Support S3 Select?    * Value: {@value}.    */
DECL|field|S3_SELECT_CAPABILITY
specifier|public
specifier|static
specifier|final
name|String
name|S3_SELECT_CAPABILITY
init|=
literal|"s3a:fs.s3a.select.sql"
decl_stmt|;
comment|/**    * Flag: is S3 select enabled?    * Value: {@value}.    */
DECL|field|FS_S3A_SELECT_ENABLED
specifier|public
specifier|static
specifier|final
name|String
name|FS_S3A_SELECT_ENABLED
init|=
name|FS_S3A_SELECT
operator|+
literal|"enabled"
decl_stmt|;
comment|/**    * Input format for data.    * Value: {@value}.    */
DECL|field|SELECT_INPUT_FORMAT
specifier|public
specifier|static
specifier|final
name|String
name|SELECT_INPUT_FORMAT
init|=
literal|"fs.s3a.select.input.format"
decl_stmt|;
comment|/**    * Output format for data -that is, what the results are generated    * as.    * Value: {@value}.    */
DECL|field|SELECT_OUTPUT_FORMAT
specifier|public
specifier|static
specifier|final
name|String
name|SELECT_OUTPUT_FORMAT
init|=
literal|"fs.s3a.select.output.format"
decl_stmt|;
comment|/**    * CSV as an input or output format: {@value}.    */
DECL|field|SELECT_FORMAT_CSV
specifier|public
specifier|static
specifier|final
name|String
name|SELECT_FORMAT_CSV
init|=
literal|"csv"
decl_stmt|;
comment|/**    * JSON as an input or output format: {@value}.    */
DECL|field|SELECT_FORMAT_JSON
specifier|public
specifier|static
specifier|final
name|String
name|SELECT_FORMAT_JSON
init|=
literal|"json"
decl_stmt|;
comment|/**    * Should Select errors include the SQL statement?    * It is easier to debug but a security risk if the exceptions    * ever get printed/logged and the query contains secrets.    */
DECL|field|SELECT_ERRORS_INCLUDE_SQL
specifier|public
specifier|static
specifier|final
name|String
name|SELECT_ERRORS_INCLUDE_SQL
init|=
name|FS_S3A_SELECT
operator|+
literal|"errors.include.sql"
decl_stmt|;
comment|/**    * How is the input compressed? This applies to all formats.    * Value: {@value}.    */
DECL|field|SELECT_INPUT_COMPRESSION
specifier|public
specifier|static
specifier|final
name|String
name|SELECT_INPUT_COMPRESSION
init|=
name|FS_S3A_SELECT
operator|+
literal|"input.compression"
decl_stmt|;
comment|/**    * No compression.    * Value: {@value}.    */
DECL|field|COMPRESSION_OPT_NONE
specifier|public
specifier|static
specifier|final
name|String
name|COMPRESSION_OPT_NONE
init|=
literal|"none"
decl_stmt|;
comment|/**    * Gzipped.    * Value: {@value}.    */
DECL|field|COMPRESSION_OPT_GZIP
specifier|public
specifier|static
specifier|final
name|String
name|COMPRESSION_OPT_GZIP
init|=
literal|"gzip"
decl_stmt|;
comment|/**    * Prefix for all CSV input options.    * Value: {@value}.    */
DECL|field|FS_S3A_SELECT_INPUT_CSV
specifier|public
specifier|static
specifier|final
name|String
name|FS_S3A_SELECT_INPUT_CSV
init|=
literal|"fs.s3a.select.input.csv."
decl_stmt|;
comment|/**    * Prefix for all CSV output options.    * Value: {@value}.    */
DECL|field|FS_S3A_SELECT_OUTPUT_CSV
specifier|public
specifier|static
specifier|final
name|String
name|FS_S3A_SELECT_OUTPUT_CSV
init|=
literal|"fs.s3a.select.output.csv."
decl_stmt|;
comment|/**    * String which indicates the row is actually a comment.    * Value: {@value}.    */
DECL|field|CSV_INPUT_COMMENT_MARKER
specifier|public
specifier|static
specifier|final
name|String
name|CSV_INPUT_COMMENT_MARKER
init|=
name|FS_S3A_SELECT_INPUT_CSV
operator|+
literal|"comment.marker"
decl_stmt|;
comment|/**    * Default marker.    * Value: {@value}.    */
DECL|field|CSV_INPUT_COMMENT_MARKER_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|CSV_INPUT_COMMENT_MARKER_DEFAULT
init|=
literal|"#"
decl_stmt|;
comment|/**    * Record delimiter. CR, LF, etc.    * Value: {@value}.    */
DECL|field|CSV_INPUT_RECORD_DELIMITER
specifier|public
specifier|static
specifier|final
name|String
name|CSV_INPUT_RECORD_DELIMITER
init|=
name|FS_S3A_SELECT_INPUT_CSV
operator|+
literal|"record.delimiter"
decl_stmt|;
comment|/**    * Default delimiter    * Value: {@value}.    */
DECL|field|CSV_INPUT_RECORD_DELIMITER_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|CSV_INPUT_RECORD_DELIMITER_DEFAULT
init|=
literal|"\n"
decl_stmt|;
comment|/**    * Field delimiter.    * Value: {@value}.    */
DECL|field|CSV_INPUT_INPUT_FIELD_DELIMITER
specifier|public
specifier|static
specifier|final
name|String
name|CSV_INPUT_INPUT_FIELD_DELIMITER
init|=
name|FS_S3A_SELECT_INPUT_CSV
operator|+
literal|"field.delimiter"
decl_stmt|;
comment|/**    * Default field delimiter.    * Value: {@value}.    */
DECL|field|CSV_INPUT_FIELD_DELIMITER_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|CSV_INPUT_FIELD_DELIMITER_DEFAULT
init|=
literal|","
decl_stmt|;
comment|/**    * Quote Character.    * Value: {@value}.    */
DECL|field|CSV_INPUT_QUOTE_CHARACTER
specifier|public
specifier|static
specifier|final
name|String
name|CSV_INPUT_QUOTE_CHARACTER
init|=
name|FS_S3A_SELECT_INPUT_CSV
operator|+
literal|"quote.character"
decl_stmt|;
comment|/**    * Default Quote Character.    * Value: {@value}.    */
DECL|field|CSV_INPUT_QUOTE_CHARACTER_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|CSV_INPUT_QUOTE_CHARACTER_DEFAULT
init|=
literal|"\""
decl_stmt|;
comment|/**    * Character to escape quotes.    * If empty: no escaping.    * Value: {@value}.    */
DECL|field|CSV_INPUT_QUOTE_ESCAPE_CHARACTER
specifier|public
specifier|static
specifier|final
name|String
name|CSV_INPUT_QUOTE_ESCAPE_CHARACTER
init|=
name|FS_S3A_SELECT_INPUT_CSV
operator|+
literal|"quote.escape.character"
decl_stmt|;
comment|/**    * Default quote escape character.    * Value: {@value}.    */
DECL|field|CSV_INPUT_QUOTE_ESCAPE_CHARACTER_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|CSV_INPUT_QUOTE_ESCAPE_CHARACTER_DEFAULT
init|=
literal|"\\"
decl_stmt|;
comment|/**    * How should headers be used?    * Value: {@value}.    */
DECL|field|CSV_INPUT_HEADER
specifier|public
specifier|static
specifier|final
name|String
name|CSV_INPUT_HEADER
init|=
name|FS_S3A_SELECT_INPUT_CSV
operator|+
literal|"header"
decl_stmt|;
comment|/**    * No header: first row is data.    * Value: {@value}.    */
DECL|field|CSV_HEADER_OPT_NONE
specifier|public
specifier|static
specifier|final
name|String
name|CSV_HEADER_OPT_NONE
init|=
literal|"none"
decl_stmt|;
comment|/**    * Ignore the header.    * Value: {@value}.    */
DECL|field|CSV_HEADER_OPT_IGNORE
specifier|public
specifier|static
specifier|final
name|String
name|CSV_HEADER_OPT_IGNORE
init|=
literal|"ignore"
decl_stmt|;
comment|/**    * Use the header.    * Value: {@value}.    */
DECL|field|CSV_HEADER_OPT_USE
specifier|public
specifier|static
specifier|final
name|String
name|CSV_HEADER_OPT_USE
init|=
literal|"use"
decl_stmt|;
comment|/**    * Default header mode: {@value}.    */
DECL|field|CSV_INPUT_HEADER_OPT_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|CSV_INPUT_HEADER_OPT_DEFAULT
init|=
name|CSV_HEADER_OPT_IGNORE
decl_stmt|;
comment|/**    * Record delimiter. CR, LF, etc.    * Value: {@value}.    */
DECL|field|CSV_OUTPUT_RECORD_DELIMITER
specifier|public
specifier|static
specifier|final
name|String
name|CSV_OUTPUT_RECORD_DELIMITER
init|=
name|FS_S3A_SELECT_OUTPUT_CSV
operator|+
literal|"record.delimiter"
decl_stmt|;
comment|/**    * Default delimiter    * Value: {@value}.    */
DECL|field|CSV_OUTPUT_RECORD_DELIMITER_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|CSV_OUTPUT_RECORD_DELIMITER_DEFAULT
init|=
literal|"\n"
decl_stmt|;
comment|/**    * Field delimiter.    * Value: {@value}.    */
DECL|field|CSV_OUTPUT_FIELD_DELIMITER
specifier|public
specifier|static
specifier|final
name|String
name|CSV_OUTPUT_FIELD_DELIMITER
init|=
name|FS_S3A_SELECT_OUTPUT_CSV
operator|+
literal|"field.delimiter"
decl_stmt|;
comment|/**    * Default field delimiter.    * Value: {@value}.    */
DECL|field|CSV_OUTPUT_FIELD_DELIMITER_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|CSV_OUTPUT_FIELD_DELIMITER_DEFAULT
init|=
literal|","
decl_stmt|;
comment|/**    * Quote Character.    * Value: {@value}.    */
DECL|field|CSV_OUTPUT_QUOTE_CHARACTER
specifier|public
specifier|static
specifier|final
name|String
name|CSV_OUTPUT_QUOTE_CHARACTER
init|=
name|FS_S3A_SELECT_OUTPUT_CSV
operator|+
literal|"quote.character"
decl_stmt|;
comment|/**    * Default Quote Character.    * Value: {@value}.    */
DECL|field|CSV_OUTPUT_QUOTE_CHARACTER_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|CSV_OUTPUT_QUOTE_CHARACTER_DEFAULT
init|=
literal|"\""
decl_stmt|;
comment|/**    * Should CSV fields be quoted?    * One of : ALWAYS, ASNEEDED    * Value: {@value}.    */
DECL|field|CSV_OUTPUT_QUOTE_FIELDS
specifier|public
specifier|static
specifier|final
name|String
name|CSV_OUTPUT_QUOTE_FIELDS
init|=
name|FS_S3A_SELECT_OUTPUT_CSV
operator|+
literal|"quote.fields"
decl_stmt|;
comment|/**    * Output quotation policy (default): {@value}.    */
DECL|field|CSV_OUTPUT_QUOTE_FIELDS_ALWAYS
specifier|public
specifier|static
specifier|final
name|String
name|CSV_OUTPUT_QUOTE_FIELDS_ALWAYS
init|=
literal|"always"
decl_stmt|;
comment|/**    * Output quotation policy: {@value}.    */
DECL|field|CSV_OUTPUT_QUOTE_FIELDS_AS_NEEEDED
specifier|public
specifier|static
specifier|final
name|String
name|CSV_OUTPUT_QUOTE_FIELDS_AS_NEEEDED
init|=
literal|"asneeded"
decl_stmt|;
comment|/**    * Character to escape quotes.    * If empty: no escaping.    * Value: {@value}.    */
DECL|field|CSV_OUTPUT_QUOTE_ESCAPE_CHARACTER
specifier|public
specifier|static
specifier|final
name|String
name|CSV_OUTPUT_QUOTE_ESCAPE_CHARACTER
init|=
name|FS_S3A_SELECT_OUTPUT_CSV
operator|+
literal|"quote.escape.character"
decl_stmt|;
comment|/**    * Default quote escape character.    * Value: {@value}.    */
DECL|field|CSV_OUTPUT_QUOTE_ESCAPE_CHARACTER_DEFAULT
specifier|public
specifier|static
specifier|final
name|String
name|CSV_OUTPUT_QUOTE_ESCAPE_CHARACTER_DEFAULT
init|=
literal|""
decl_stmt|;
block|}
end_class

end_unit

