begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.filecache
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|filecache
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
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|FSDataOutputStream
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
name|FileStatus
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
name|FileSystem
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_class
DECL|class|TestDistributedCache
specifier|public
class|class
name|TestDistributedCache
extends|extends
name|TestCase
block|{
DECL|field|LOCAL_FS
specifier|static
specifier|final
name|URI
name|LOCAL_FS
init|=
name|URI
operator|.
name|create
argument_list|(
literal|"file:///"
argument_list|)
decl_stmt|;
DECL|field|TEST_CACHE_BASE_DIR
specifier|private
specifier|static
name|String
name|TEST_CACHE_BASE_DIR
init|=
operator|new
name|Path
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/tmp/cachebasedir"
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|replace
argument_list|(
literal|' '
argument_list|,
literal|'+'
argument_list|)
decl_stmt|;
DECL|field|TEST_ROOT_DIR
specifier|private
specifier|static
name|String
name|TEST_ROOT_DIR
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/tmp/distributedcache"
argument_list|)
decl_stmt|;
DECL|field|TEST_FILE_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|TEST_FILE_SIZE
init|=
literal|4
operator|*
literal|1024
decl_stmt|;
comment|// 4K
DECL|field|LOCAL_CACHE_LIMIT
specifier|private
specifier|static
specifier|final
name|int
name|LOCAL_CACHE_LIMIT
init|=
literal|5
operator|*
literal|1024
decl_stmt|;
comment|//5K
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|firstCacheFile
specifier|private
name|Path
name|firstCacheFile
decl_stmt|;
DECL|field|secondCacheFile
specifier|private
name|Path
name|secondCacheFile
decl_stmt|;
DECL|field|localfs
specifier|private
name|FileSystem
name|localfs
decl_stmt|;
comment|/**    * @see TestCase#setUp()    */
annotation|@
name|Override
DECL|method|setUp ()
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
literal|"local.cache.size"
argument_list|,
name|LOCAL_CACHE_LIMIT
argument_list|)
expr_stmt|;
name|localfs
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
name|LOCAL_FS
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|firstCacheFile
operator|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
operator|+
literal|"/firstcachefile"
argument_list|)
expr_stmt|;
name|secondCacheFile
operator|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
operator|+
literal|"/secondcachefile"
argument_list|)
expr_stmt|;
name|createTempFile
argument_list|(
name|localfs
argument_list|,
name|firstCacheFile
argument_list|)
expr_stmt|;
name|createTempFile
argument_list|(
name|localfs
argument_list|,
name|secondCacheFile
argument_list|)
expr_stmt|;
block|}
comment|/** test delete cache */
DECL|method|testDeleteCache ()
specifier|public
name|void
name|testDeleteCache
parameter_list|()
throws|throws
name|Exception
block|{
name|DistributedCache
operator|.
name|getLocalCache
argument_list|(
name|firstCacheFile
operator|.
name|toUri
argument_list|()
argument_list|,
name|conf
argument_list|,
operator|new
name|Path
argument_list|(
name|TEST_CACHE_BASE_DIR
argument_list|)
argument_list|,
literal|false
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|)
argument_list|)
expr_stmt|;
name|DistributedCache
operator|.
name|releaseCache
argument_list|(
name|firstCacheFile
operator|.
name|toUri
argument_list|()
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|//in above code,localized a file of size 4K and then release the cache which will cause the cache
comment|//be deleted when the limit goes out. The below code localize another cache which's designed to
comment|//sweep away the first cache.
name|DistributedCache
operator|.
name|getLocalCache
argument_list|(
name|secondCacheFile
operator|.
name|toUri
argument_list|()
argument_list|,
name|conf
argument_list|,
operator|new
name|Path
argument_list|(
name|TEST_CACHE_BASE_DIR
argument_list|)
argument_list|,
literal|false
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|)
argument_list|)
expr_stmt|;
name|FileStatus
index|[]
name|dirStatuses
init|=
name|localfs
operator|.
name|listStatus
argument_list|(
operator|new
name|Path
argument_list|(
name|TEST_CACHE_BASE_DIR
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"DistributedCache failed deleting old cache when the cache store is full."
argument_list|,
name|dirStatuses
operator|.
name|length
operator|>
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|createTempFile (FileSystem fs, Path p)
specifier|private
name|void
name|createTempFile
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|p
parameter_list|)
throws|throws
name|IOException
block|{
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|byte
index|[]
name|toWrite
init|=
operator|new
name|byte
index|[
name|TEST_FILE_SIZE
index|]
decl_stmt|;
operator|new
name|Random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|toWrite
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|toWrite
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|FileSystem
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"created: "
operator|+
name|p
operator|+
literal|", size="
operator|+
name|TEST_FILE_SIZE
argument_list|)
expr_stmt|;
block|}
comment|/**    * @see TestCase#tearDown()    */
annotation|@
name|Override
DECL|method|tearDown ()
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|IOException
block|{
name|localfs
operator|.
name|delete
argument_list|(
name|firstCacheFile
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|localfs
operator|.
name|delete
argument_list|(
name|secondCacheFile
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|localfs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

