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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assume
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|conf
operator|.
name|Configuration
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
name|fs
operator|.
name|CommonConfigurationKeys
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
name|fs
operator|.
name|FSDataInputStream
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
name|fs
operator|.
name|FileContext
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
name|fs
operator|.
name|Path
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
name|fs
operator|.
name|PathIOException
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
name|fs
operator|.
name|s3a
operator|.
name|impl
operator|.
name|ChangeDetectionPolicy
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
name|fs
operator|.
name|s3a
operator|.
name|impl
operator|.
name|ChangeDetectionPolicy
operator|.
name|Source
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
name|fs
operator|.
name|s3a
operator|.
name|S3AFileSystem
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
name|fs
operator|.
name|s3a
operator|.
name|S3AInstrumentation
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
name|fs
operator|.
name|s3a
operator|.
name|S3ATestUtils
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
name|fs
operator|.
name|s3a
operator|.
name|Statistic
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
name|mapred
operator|.
name|JobConf
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
name|util
operator|.
name|DurationInfo
import|;
end_import

begin_import
import|import static
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
name|S3ATestUtils
operator|.
name|assume
import|;
end_import

begin_import
import|import static
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
name|S3ATestUtils
operator|.
name|getTestPropertyBool
import|;
end_import

begin_import
import|import static
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
name|scale
operator|.
name|S3AScaleTestBase
operator|.
name|_1KB
import|;
end_import

begin_import
import|import static
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
name|scale
operator|.
name|S3AScaleTestBase
operator|.
name|_1MB
import|;
end_import

begin_import
import|import static
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
operator|.
name|SelectConstants
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|LambdaTestUtils
operator|.
name|intercept
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|containsString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|not
import|;
end_import

begin_comment
comment|/**  * Test the S3 Select feature with the Landsat dataset.  *  * This helps explore larger datasets, compression and the like.  *  * This suite is only executed if the destination store declares its support for  * the feature and the test CSV file configuration option points to the  * standard landsat GZip file. That's because these tests require the specific  * format of the landsat file.  *  * Normally working with the landsat file is a scale test.  * Here, because of the select operations, there's a lot less data  * to download.  * For this to work: write aggressive select calls: filtering, using LIMIT  * and projecting down to a few columns.  *  * For the structure, see  *<a href="https://docs.opendata.aws/landsat-pds/readme.html">Landsat on AWS</a>  *  *<code>  *   entityId: String         LC80101172015002LGN00  *   acquisitionDate: String  2015-01-02 15:49:05.571384  *   cloudCover: Float (possibly -ve) 80.81  *   processingLevel: String  L1GT  *   path: Int                10  *   row:  Int                117  *   min_lat: Float           -79.09923  *   min_lon: Float           -139.66082  *   max_lat: Float           -77.7544  *   max_lon: Float           125.09297  *   download_url: HTTPS URL https://s3-us-west-2.amazonaws.com/landsat-pds/L8/010/117/LC80101172015002LGN00/index.html  *</code>  * Ranges  *<ol>  *<li>Latitude should range in -180<= lat<= 180</li>  *<li>Longitude in 0<= lon<= 360</li>  *<li>Standard Greenwich Meridian (not the french one which still surfaces)</li>  *<li>Cloud cover<i>Should</i> be 0-100, but there are some negative ones.</li>  *</ol>  *  * Head of the file:  *<code>  entityId,acquisitionDate,cloudCover,processingLevel,path,row,min_lat,min_lon,max_lat,max_lon,download_url  * LC80101172015002LGN00,2015-01-02 15:49:05.571384,80.81,L1GT,10,117,-79.09923,-139.66082,-77.7544,-125.09297,https://s3-us-west-2.amazonaws.com/landsat-pds/L8/010/117/LC80101172015002LGN00/index.html  * LC80260392015002LGN00,2015-01-02 16:56:51.399666,90.84,L1GT,26,39,29.23106,-97.48576,31.36421,-95.16029,https://s3-us-west-2.amazonaws.com/landsat-pds/L8/026/039/LC80260392015002LGN00/index.html  * LC82270742015002LGN00,2015-01-02 13:53:02.047000,83.44,L1GT,227,74,-21.28598,-59.27736,-19.17398,-57.07423,https://s3-us-west-2.amazonaws.com/landsat-pds/L8/227/074/LC82270742015002LGN00/index.html  * LC82270732015002LGN00,2015-01-02 13:52:38.110317,52.29,L1T,227,73,-19.84365,-58.93258,-17.73324,-56.74692,https://s3-us-west-2.amazonaws.com/landsat-pds/L8/227/073/LC82270732015002LGN00/index.html  *</code>  *  * For the Curious this is the Scala/Spark declaration of the schema.  *<code>  *   def addLandsatColumns(csv: DataFrame): DataFrame = {  *     csv  *       .withColumnRenamed("entityId", "id")  *       .withColumn("acquisitionDate",  *         csv.col("acquisitionDate").cast(TimestampType))  *       .withColumn("cloudCover", csv.col("cloudCover").cast(DoubleType))  *       .withColumn("path", csv.col("path").cast(IntegerType))  *       .withColumn("row", csv.col("row").cast(IntegerType))  *       .withColumn("min_lat", csv.col("min_lat").cast(DoubleType))  *       .withColumn("min_lon", csv.col("min_lon").cast(DoubleType))  *       .withColumn("max_lat", csv.col("max_lat").cast(DoubleType))  *       .withColumn("max_lon", csv.col("max_lon").cast(DoubleType))  *       .withColumn("year",  *         year(col("acquisitionDate")))  *       .withColumn("month",  *         month(col("acquisitionDate")))  *       .withColumn("day",  *         month(col("acquisitionDate")))  *   }  *</code>  */
end_comment

begin_class
DECL|class|ITestS3SelectLandsat
specifier|public
class|class
name|ITestS3SelectLandsat
extends|extends
name|AbstractS3SelectTest
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ITestS3SelectLandsat
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|selectConf
specifier|private
name|JobConf
name|selectConf
decl_stmt|;
comment|/**    * Normal limit for select operations.    * Value: {@value}.    */
DECL|field|SELECT_LIMIT
specifier|public
specifier|static
specifier|final
name|int
name|SELECT_LIMIT
init|=
literal|250
decl_stmt|;
comment|/**    * And that select limit as a limit string.    */
DECL|field|LIMITED
specifier|public
specifier|static
specifier|final
name|String
name|LIMITED
init|=
literal|" LIMIT "
operator|+
name|SELECT_LIMIT
decl_stmt|;
comment|/**    * Select days with 100% cloud cover, limited to {@link #SELECT_LIMIT}.    * Value: {@value}.    */
DECL|field|SELECT_ENTITY_ID_ALL_CLOUDS
specifier|public
specifier|static
specifier|final
name|String
name|SELECT_ENTITY_ID_ALL_CLOUDS
init|=
literal|"SELECT\n"
operator|+
literal|"s.entityId from\n"
operator|+
literal|"S3OBJECT s WHERE\n"
operator|+
literal|"s.\"cloudCover\" = '100.0'\n"
operator|+
name|LIMITED
decl_stmt|;
comment|/**    * Select sunny days. There's no limit on the returned values, so    * set one except for a scale test.    * Value: {@value}.    */
DECL|field|SELECT_SUNNY_ROWS_NO_LIMIT
specifier|public
specifier|static
specifier|final
name|String
name|SELECT_SUNNY_ROWS_NO_LIMIT
init|=
literal|"SELECT * FROM S3OBJECT s WHERE s.cloudCover = '0.0'"
decl_stmt|;
comment|/**    * A Select call which returns nothing, always.    * Value: {@value}.    */
DECL|field|SELECT_NOTHING
specifier|public
specifier|static
specifier|final
name|String
name|SELECT_NOTHING
init|=
literal|"SELECT * FROM S3OBJECT s WHERE s.cloudCover = 'sunny'"
decl_stmt|;
comment|/**    * Select the processing level; no limit.    * Value: {@value}.    */
DECL|field|SELECT_PROCESSING_LEVEL_NO_LIMIT
specifier|public
specifier|static
specifier|final
name|String
name|SELECT_PROCESSING_LEVEL_NO_LIMIT
init|=
literal|"SELECT\n"
operator|+
literal|"s.processingLevel from\n"
operator|+
literal|"S3OBJECT s"
decl_stmt|;
annotation|@
name|Override
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setup
argument_list|()
expr_stmt|;
name|selectConf
operator|=
operator|new
name|JobConf
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// file is compressed.
name|selectConf
operator|.
name|set
argument_list|(
name|SELECT_INPUT_COMPRESSION
argument_list|,
name|COMPRESSION_OPT_GZIP
argument_list|)
expr_stmt|;
comment|// and has a header
name|selectConf
operator|.
name|set
argument_list|(
name|CSV_INPUT_HEADER
argument_list|,
name|CSV_HEADER_OPT_USE
argument_list|)
expr_stmt|;
name|selectConf
operator|.
name|setBoolean
argument_list|(
name|SELECT_ERRORS_INCLUDE_SQL
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|inputMust
argument_list|(
name|selectConf
argument_list|,
name|CSV_INPUT_HEADER
argument_list|,
name|CSV_HEADER_OPT_USE
argument_list|)
expr_stmt|;
name|inputMust
argument_list|(
name|selectConf
argument_list|,
name|SELECT_INPUT_FORMAT
argument_list|,
name|SELECT_FORMAT_CSV
argument_list|)
expr_stmt|;
name|inputMust
argument_list|(
name|selectConf
argument_list|,
name|SELECT_OUTPUT_FORMAT
argument_list|,
name|SELECT_FORMAT_CSV
argument_list|)
expr_stmt|;
name|inputMust
argument_list|(
name|selectConf
argument_list|,
name|SELECT_INPUT_COMPRESSION
argument_list|,
name|COMPRESSION_OPT_GZIP
argument_list|)
expr_stmt|;
comment|// disable the gzip codec, so that the record readers do not
comment|// get confused
name|enablePassthroughCodec
argument_list|(
name|selectConf
argument_list|,
literal|".gz"
argument_list|)
expr_stmt|;
name|ChangeDetectionPolicy
name|changeDetectionPolicy
init|=
name|getLandsatFS
argument_list|()
operator|.
name|getChangeDetectionPolicy
argument_list|()
decl_stmt|;
name|Assume
operator|.
name|assumeFalse
argument_list|(
literal|"the standard landsat bucket doesn't have versioning"
argument_list|,
name|changeDetectionPolicy
operator|.
name|getSource
argument_list|()
operator|==
name|Source
operator|.
name|VersionId
operator|&&
name|changeDetectionPolicy
operator|.
name|isRequireVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getMaxLines ()
specifier|protected
name|int
name|getMaxLines
parameter_list|()
block|{
return|return
name|SELECT_LIMIT
operator|*
literal|2
return|;
block|}
annotation|@
name|Test
DECL|method|testSelectCloudcoverIgnoreHeader ()
specifier|public
name|void
name|testSelectCloudcoverIgnoreHeader
parameter_list|()
throws|throws
name|Throwable
block|{
name|describe
argument_list|(
literal|"select ignoring the header"
argument_list|)
expr_stmt|;
name|selectConf
operator|.
name|set
argument_list|(
name|CSV_INPUT_HEADER
argument_list|,
name|CSV_HEADER_OPT_IGNORE
argument_list|)
expr_stmt|;
name|String
name|sql
init|=
literal|"SELECT\n"
operator|+
literal|"* from\n"
operator|+
literal|"S3OBJECT s WHERE\n"
operator|+
literal|"s._3 = '0.0'\n"
operator|+
name|LIMITED
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
name|selectLandsatFile
argument_list|(
name|selectConf
argument_list|,
name|sql
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Line count: {}"
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|verifySelectionCount
argument_list|(
literal|1
argument_list|,
name|SELECT_LIMIT
argument_list|,
name|sql
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSelectCloudcoverUseHeader ()
specifier|public
name|void
name|testSelectCloudcoverUseHeader
parameter_list|()
throws|throws
name|Throwable
block|{
name|describe
argument_list|(
literal|"select 100% cover using the header, "
operator|+
literal|"+ verify projection and incrementing select statistics"
argument_list|)
expr_stmt|;
name|S3ATestUtils
operator|.
name|MetricDiff
name|selectCount
init|=
operator|new
name|S3ATestUtils
operator|.
name|MetricDiff
argument_list|(
name|getLandsatFS
argument_list|()
argument_list|,
name|Statistic
operator|.
name|OBJECT_SELECT_REQUESTS
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
name|selectLandsatFile
argument_list|(
name|selectConf
argument_list|,
name|SELECT_ENTITY_ID_ALL_CLOUDS
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Line count: {}"
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|verifySelectionCount
argument_list|(
literal|1
argument_list|,
name|SELECT_LIMIT
argument_list|,
name|SELECT_ENTITY_ID_ALL_CLOUDS
argument_list|,
name|list
argument_list|)
expr_stmt|;
name|String
name|line1
init|=
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
literal|"no column filtering from "
operator|+
name|SELECT_ENTITY_ID_ALL_CLOUDS
argument_list|,
name|line1
argument_list|,
name|not
argument_list|(
name|containsString
argument_list|(
literal|"100.0"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|selectCount
operator|.
name|assertDiffEquals
argument_list|(
literal|"select count"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFileContextIntegration ()
specifier|public
name|void
name|testFileContextIntegration
parameter_list|()
throws|throws
name|Throwable
block|{
name|describe
argument_list|(
literal|"Test that select works through FileContext"
argument_list|)
expr_stmt|;
name|FileContext
name|fc
init|=
name|S3ATestUtils
operator|.
name|createTestFileContext
argument_list|(
name|getConfiguration
argument_list|()
argument_list|)
decl_stmt|;
comment|// there's a limit on the number of rows to read; this is larger
comment|// than the SELECT_LIMIT call to catch any failure where more than
comment|// that is returned, newline parsing fails, etc etc.
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
name|parseToLines
argument_list|(
name|select
argument_list|(
name|fc
argument_list|,
name|getLandsatGZ
argument_list|()
argument_list|,
name|selectConf
argument_list|,
name|SELECT_ENTITY_ID_ALL_CLOUDS
argument_list|)
argument_list|,
name|SELECT_LIMIT
operator|*
literal|2
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Line count: {}"
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|verifySelectionCount
argument_list|(
literal|1
argument_list|,
name|SELECT_LIMIT
argument_list|,
name|SELECT_ENTITY_ID_ALL_CLOUDS
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReadLandsatRecords ()
specifier|public
name|void
name|testReadLandsatRecords
parameter_list|()
throws|throws
name|Throwable
block|{
name|describe
argument_list|(
literal|"Use a record reader to read the records"
argument_list|)
expr_stmt|;
name|inputMust
argument_list|(
name|selectConf
argument_list|,
name|CSV_OUTPUT_FIELD_DELIMITER
argument_list|,
literal|"\\t"
argument_list|)
expr_stmt|;
name|inputMust
argument_list|(
name|selectConf
argument_list|,
name|CSV_OUTPUT_QUOTE_CHARACTER
argument_list|,
literal|"'"
argument_list|)
expr_stmt|;
name|inputMust
argument_list|(
name|selectConf
argument_list|,
name|CSV_OUTPUT_QUOTE_FIELDS
argument_list|,
name|CSV_OUTPUT_QUOTE_FIELDS_AS_NEEEDED
argument_list|)
expr_stmt|;
name|inputMust
argument_list|(
name|selectConf
argument_list|,
name|CSV_OUTPUT_RECORD_DELIMITER
argument_list|,
literal|"\n"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|records
init|=
name|readRecords
argument_list|(
name|selectConf
argument_list|,
name|getLandsatGZ
argument_list|()
argument_list|,
name|SELECT_ENTITY_ID_ALL_CLOUDS
argument_list|,
name|createLineRecordReader
argument_list|()
argument_list|,
name|SELECT_LIMIT
argument_list|)
decl_stmt|;
name|verifySelectionCount
argument_list|(
literal|1
argument_list|,
name|SELECT_LIMIT
argument_list|,
name|SELECT_ENTITY_ID_ALL_CLOUDS
argument_list|,
name|records
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReadLandsatRecordsNoMatch ()
specifier|public
name|void
name|testReadLandsatRecordsNoMatch
parameter_list|()
throws|throws
name|Throwable
block|{
name|describe
argument_list|(
literal|"Verify the v2 record reader does not fail"
operator|+
literal|" when there are no results"
argument_list|)
expr_stmt|;
name|verifySelectionCount
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|SELECT_NOTHING
argument_list|,
name|readRecords
argument_list|(
name|selectConf
argument_list|,
name|getLandsatGZ
argument_list|()
argument_list|,
name|SELECT_NOTHING
argument_list|,
name|createLineRecordReader
argument_list|()
argument_list|,
name|SELECT_LIMIT
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReadLandsatRecordsGZipEnabled ()
specifier|public
name|void
name|testReadLandsatRecordsGZipEnabled
parameter_list|()
throws|throws
name|Throwable
block|{
name|describe
argument_list|(
literal|"Verify that by default, the gzip codec is connected to .gz"
operator|+
literal|" files, and so fails"
argument_list|)
expr_stmt|;
comment|// implicitly re-enable the gzip codec.
name|selectConf
operator|.
name|unset
argument_list|(
name|CommonConfigurationKeys
operator|.
name|IO_COMPRESSION_CODECS_KEY
argument_list|)
expr_stmt|;
name|intercept
argument_list|(
name|IOException
operator|.
name|class
argument_list|,
literal|"gzip"
argument_list|,
parameter_list|()
lambda|->
name|readRecords
argument_list|(
name|selectConf
argument_list|,
name|getLandsatGZ
argument_list|()
argument_list|,
name|SELECT_ENTITY_ID_ALL_CLOUDS
argument_list|,
name|createLineRecordReader
argument_list|()
argument_list|,
name|SELECT_LIMIT
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReadLandsatRecordsV1 ()
specifier|public
name|void
name|testReadLandsatRecordsV1
parameter_list|()
throws|throws
name|Throwable
block|{
name|describe
argument_list|(
literal|"Use a record reader to read the records"
argument_list|)
expr_stmt|;
name|verifySelectionCount
argument_list|(
literal|1
argument_list|,
name|SELECT_LIMIT
argument_list|,
name|SELECT_ENTITY_ID_ALL_CLOUDS
argument_list|,
name|readRecords
argument_list|(
name|selectConf
argument_list|,
name|getLandsatGZ
argument_list|()
argument_list|,
name|SELECT_ENTITY_ID_ALL_CLOUDS
argument_list|,
name|createLineRecordReader
argument_list|()
argument_list|,
name|SELECT_LIMIT
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReadLandsatRecordsV1NoResults ()
specifier|public
name|void
name|testReadLandsatRecordsV1NoResults
parameter_list|()
throws|throws
name|Throwable
block|{
name|describe
argument_list|(
literal|"verify that a select with no results is not an error"
argument_list|)
expr_stmt|;
name|verifySelectionCount
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|SELECT_NOTHING
argument_list|,
name|readRecords
argument_list|(
name|selectConf
argument_list|,
name|getLandsatGZ
argument_list|()
argument_list|,
name|SELECT_NOTHING
argument_list|,
name|createLineRecordReader
argument_list|()
argument_list|,
name|SELECT_LIMIT
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Select from the landsat file.    * @param conf config for the select call.    * @param sql template for a formatted SQL request.    * @param args arguments for the formatted request.    * @return the lines selected    * @throws IOException failure    */
DECL|method|selectLandsatFile ( final Configuration conf, final String sql, final Object... args)
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|selectLandsatFile
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|String
name|sql
parameter_list|,
specifier|final
name|Object
modifier|...
name|args
parameter_list|)
throws|throws
name|Exception
block|{
comment|// there's a limit on the number of rows to read; this is larger
comment|// than the SELECT_LIMIT call to catch any failure where more than
comment|// that is returned, newline parsing fails, etc etc.
return|return
name|parseToLines
argument_list|(
name|select
argument_list|(
name|getLandsatFS
argument_list|()
argument_list|,
name|getLandsatGZ
argument_list|()
argument_list|,
name|conf
argument_list|,
name|sql
argument_list|,
name|args
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * This is a larger-scale version of {@link ITestS3Select#testSelectSeek()}.    */
annotation|@
name|Test
DECL|method|testSelectSeekFullLandsat ()
specifier|public
name|void
name|testSelectSeekFullLandsat
parameter_list|()
throws|throws
name|Throwable
block|{
name|describe
argument_list|(
literal|"Verify forward seeks work, not others"
argument_list|)
expr_stmt|;
name|boolean
name|enabled
init|=
name|getTestPropertyBool
argument_list|(
name|getConfiguration
argument_list|()
argument_list|,
name|KEY_SCALE_TESTS_ENABLED
argument_list|,
name|DEFAULT_SCALE_TESTS_ENABLED
argument_list|)
decl_stmt|;
name|assume
argument_list|(
literal|"Scale test disabled"
argument_list|,
name|enabled
argument_list|)
expr_stmt|;
comment|// start: read in the full data through the initial select
comment|// this makes asserting that contents match possible
specifier|final
name|Path
name|path
init|=
name|getLandsatGZ
argument_list|()
decl_stmt|;
name|S3AFileSystem
name|fs
init|=
name|getLandsatFS
argument_list|()
decl_stmt|;
name|int
name|len
init|=
operator|(
name|int
operator|)
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
operator|.
name|getLen
argument_list|()
decl_stmt|;
name|byte
index|[]
name|dataset
init|=
operator|new
name|byte
index|[
literal|4
operator|*
name|_1MB
index|]
decl_stmt|;
name|int
name|actualLen
decl_stmt|;
try|try
init|(
name|DurationInfo
name|ignored
init|=
operator|new
name|DurationInfo
argument_list|(
name|LOG
argument_list|,
literal|"Initial read of %s"
argument_list|,
name|path
argument_list|)
init|;
name|FSDataInputStream
name|sourceStream
operator|=
name|select
argument_list|(
name|fs
argument_list|,
name|path
argument_list|,
name|selectConf
argument_list|,
name|SELECT_EVERYTHING
argument_list|)
init|)
block|{
comment|// read it in
name|actualLen
operator|=
name|IOUtils
operator|.
name|read
argument_list|(
name|sourceStream
argument_list|,
name|dataset
argument_list|)
expr_stmt|;
block|}
name|int
name|seekRange
init|=
literal|16
operator|*
name|_1KB
decl_stmt|;
try|try
init|(
name|FSDataInputStream
name|seekStream
init|=
name|select
argument_list|(
name|fs
argument_list|,
name|path
argument_list|,
name|selectConf
argument_list|,
name|SELECT_EVERYTHING
argument_list|)
init|)
block|{
name|SelectInputStream
name|sis
init|=
operator|(
name|SelectInputStream
operator|)
name|seekStream
operator|.
name|getWrappedStream
argument_list|()
decl_stmt|;
name|S3AInstrumentation
operator|.
name|InputStreamStatistics
name|streamStats
init|=
name|sis
operator|.
name|getS3AStreamStatistics
argument_list|()
decl_stmt|;
comment|// lazy seek doesn't raise a problem here
name|seekStream
operator|.
name|seek
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"first byte read"
argument_list|,
name|dataset
index|[
literal|0
index|]
argument_list|,
name|seekStream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
comment|// and now the pos has moved, again, seek will be OK
name|seekStream
operator|.
name|seek
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|seekStream
operator|.
name|seek
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// but trying to seek elsewhere now fails
name|intercept
argument_list|(
name|PathIOException
operator|.
name|class
argument_list|,
name|SelectInputStream
operator|.
name|SEEK_UNSUPPORTED
argument_list|,
parameter_list|()
lambda|->
name|seekStream
operator|.
name|seek
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// positioned reads from the current location work.
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|1
index|]
decl_stmt|;
name|seekStream
operator|.
name|readFully
argument_list|(
name|seekStream
operator|.
name|getPos
argument_list|()
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
comment|// but positioned backwards fail.
name|intercept
argument_list|(
name|PathIOException
operator|.
name|class
argument_list|,
name|SelectInputStream
operator|.
name|SEEK_UNSUPPORTED
argument_list|,
parameter_list|()
lambda|->
name|seekStream
operator|.
name|readFully
argument_list|(
literal|0
argument_list|,
name|buffer
argument_list|)
argument_list|)
expr_stmt|;
comment|// forward seeks are implemented as 1+ skip
name|long
name|target
init|=
name|seekStream
operator|.
name|getPos
argument_list|()
operator|+
name|seekRange
decl_stmt|;
name|seek
argument_list|(
name|seekStream
argument_list|,
name|target
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Seek position in "
operator|+
name|seekStream
argument_list|,
name|target
argument_list|,
name|seekStream
operator|.
name|getPos
argument_list|()
argument_list|)
expr_stmt|;
comment|// now do a read and compare values
name|assertEquals
argument_list|(
literal|"byte at seek position"
argument_list|,
name|dataset
index|[
operator|(
name|int
operator|)
name|seekStream
operator|.
name|getPos
argument_list|()
index|]
argument_list|,
name|seekStream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Seek bytes skipped in "
operator|+
name|streamStats
argument_list|,
name|seekRange
argument_list|,
name|streamStats
operator|.
name|bytesSkippedOnSeek
argument_list|)
expr_stmt|;
name|long
name|offset
decl_stmt|;
name|long
name|increment
init|=
literal|64
operator|*
name|_1KB
decl_stmt|;
comment|// seek forward, comparing bytes
for|for
control|(
name|offset
operator|=
literal|32
operator|*
name|_1KB
init|;
name|offset
operator|<
name|actualLen
condition|;
name|offset
operator|+=
name|increment
control|)
block|{
name|seek
argument_list|(
name|seekStream
argument_list|,
name|offset
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Seek position in "
operator|+
name|seekStream
argument_list|,
name|offset
argument_list|,
name|seekStream
operator|.
name|getPos
argument_list|()
argument_list|)
expr_stmt|;
comment|// now do a read and compare values
name|assertEquals
argument_list|(
literal|"byte at seek position"
argument_list|,
name|dataset
index|[
operator|(
name|int
operator|)
name|seekStream
operator|.
name|getPos
argument_list|()
index|]
argument_list|,
name|seekStream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
init|;
name|offset
operator|<
name|len
condition|;
name|offset
operator|+=
name|_1MB
control|)
block|{
name|seek
argument_list|(
name|seekStream
argument_list|,
name|offset
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Seek position in "
operator|+
name|seekStream
argument_list|,
name|offset
argument_list|,
name|seekStream
operator|.
name|getPos
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// there's no knowledge of how much data is left, but with Gzip
comment|// involved there can be a lot. To keep the test duration down,
comment|// this test, unlike the simpler one, doesn't try to read past the
comment|// EOF. Know this: it will be slow.
name|LOG
operator|.
name|info
argument_list|(
literal|"Seek statistics {}"
argument_list|,
name|streamStats
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

