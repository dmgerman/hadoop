begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|localizer
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|any
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|times
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|verify
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentMap
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ContainerId
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|LocalResourceType
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|LocalResourceVisibility
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
name|yarn
operator|.
name|event
operator|.
name|Dispatcher
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
name|yarn
operator|.
name|event
operator|.
name|DrainDispatcher
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
name|yarn
operator|.
name|event
operator|.
name|EventHandler
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
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|DeletionService
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
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|container
operator|.
name|ContainerEventType
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
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|localizer
operator|.
name|event
operator|.
name|LocalizerEvent
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
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|localizer
operator|.
name|event
operator|.
name|LocalizerEventType
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
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|localizer
operator|.
name|event
operator|.
name|LocalizerResourceRequestEvent
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
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|localizer
operator|.
name|event
operator|.
name|ResourceEvent
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
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|localizer
operator|.
name|event
operator|.
name|ResourceLocalizedEvent
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
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|localizer
operator|.
name|event
operator|.
name|ResourceReleaseEvent
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
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|localizer
operator|.
name|event
operator|.
name|ResourceRequestEvent
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
name|yarn
operator|.
name|util
operator|.
name|BuilderUtils
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
name|mortbay
operator|.
name|log
operator|.
name|Log
import|;
end_import

begin_class
DECL|class|TestLocalResourcesTrackerImpl
specifier|public
class|class
name|TestLocalResourcesTrackerImpl
block|{
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|test ()
specifier|public
name|void
name|test
parameter_list|()
block|{
name|String
name|user
init|=
literal|"testuser"
decl_stmt|;
name|DrainDispatcher
name|dispatcher
init|=
literal|null
decl_stmt|;
try|try
block|{
name|dispatcher
operator|=
name|createDispatcher
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|EventHandler
argument_list|<
name|LocalizerEvent
argument_list|>
name|localizerEventHandler
init|=
name|mock
argument_list|(
name|EventHandler
operator|.
name|class
argument_list|)
decl_stmt|;
name|EventHandler
argument_list|<
name|LocalizerEvent
argument_list|>
name|containerEventHandler
init|=
name|mock
argument_list|(
name|EventHandler
operator|.
name|class
argument_list|)
decl_stmt|;
name|dispatcher
operator|.
name|register
argument_list|(
name|LocalizerEventType
operator|.
name|class
argument_list|,
name|localizerEventHandler
argument_list|)
expr_stmt|;
name|dispatcher
operator|.
name|register
argument_list|(
name|ContainerEventType
operator|.
name|class
argument_list|,
name|containerEventHandler
argument_list|)
expr_stmt|;
name|DeletionService
name|mockDelService
init|=
name|mock
argument_list|(
name|DeletionService
operator|.
name|class
argument_list|)
decl_stmt|;
name|ContainerId
name|cId1
init|=
name|BuilderUtils
operator|.
name|newContainerId
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|LocalizerContext
name|lc1
init|=
operator|new
name|LocalizerContext
argument_list|(
name|user
argument_list|,
name|cId1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|ContainerId
name|cId2
init|=
name|BuilderUtils
operator|.
name|newContainerId
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|LocalizerContext
name|lc2
init|=
operator|new
name|LocalizerContext
argument_list|(
name|user
argument_list|,
name|cId2
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|LocalResourceRequest
name|req1
init|=
name|createLocalResourceRequest
argument_list|(
name|user
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
name|LocalResourceVisibility
operator|.
name|PUBLIC
argument_list|)
decl_stmt|;
name|LocalResourceRequest
name|req2
init|=
name|createLocalResourceRequest
argument_list|(
name|user
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|,
name|LocalResourceVisibility
operator|.
name|PUBLIC
argument_list|)
decl_stmt|;
name|LocalizedResource
name|lr1
init|=
name|createLocalizedResource
argument_list|(
name|req1
argument_list|,
name|dispatcher
argument_list|)
decl_stmt|;
name|LocalizedResource
name|lr2
init|=
name|createLocalizedResource
argument_list|(
name|req2
argument_list|,
name|dispatcher
argument_list|)
decl_stmt|;
name|ConcurrentMap
argument_list|<
name|LocalResourceRequest
argument_list|,
name|LocalizedResource
argument_list|>
name|localrsrc
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|LocalResourceRequest
argument_list|,
name|LocalizedResource
argument_list|>
argument_list|()
decl_stmt|;
name|localrsrc
operator|.
name|put
argument_list|(
name|req1
argument_list|,
name|lr1
argument_list|)
expr_stmt|;
name|localrsrc
operator|.
name|put
argument_list|(
name|req2
argument_list|,
name|lr2
argument_list|)
expr_stmt|;
name|LocalResourcesTracker
name|tracker
init|=
operator|new
name|LocalResourcesTrackerImpl
argument_list|(
name|user
argument_list|,
name|dispatcher
argument_list|,
name|localrsrc
argument_list|)
decl_stmt|;
name|ResourceEvent
name|req11Event
init|=
operator|new
name|ResourceRequestEvent
argument_list|(
name|req1
argument_list|,
name|LocalResourceVisibility
operator|.
name|PUBLIC
argument_list|,
name|lc1
argument_list|)
decl_stmt|;
name|ResourceEvent
name|req12Event
init|=
operator|new
name|ResourceRequestEvent
argument_list|(
name|req1
argument_list|,
name|LocalResourceVisibility
operator|.
name|PUBLIC
argument_list|,
name|lc2
argument_list|)
decl_stmt|;
name|ResourceEvent
name|req21Event
init|=
operator|new
name|ResourceRequestEvent
argument_list|(
name|req2
argument_list|,
name|LocalResourceVisibility
operator|.
name|PUBLIC
argument_list|,
name|lc1
argument_list|)
decl_stmt|;
name|ResourceEvent
name|rel11Event
init|=
operator|new
name|ResourceReleaseEvent
argument_list|(
name|req1
argument_list|,
name|cId1
argument_list|)
decl_stmt|;
name|ResourceEvent
name|rel12Event
init|=
operator|new
name|ResourceReleaseEvent
argument_list|(
name|req1
argument_list|,
name|cId2
argument_list|)
decl_stmt|;
name|ResourceEvent
name|rel21Event
init|=
operator|new
name|ResourceReleaseEvent
argument_list|(
name|req2
argument_list|,
name|cId1
argument_list|)
decl_stmt|;
comment|// Localize R1 for C1
name|tracker
operator|.
name|handle
argument_list|(
name|req11Event
argument_list|)
expr_stmt|;
comment|// Localize R1 for C2
name|tracker
operator|.
name|handle
argument_list|(
name|req12Event
argument_list|)
expr_stmt|;
comment|// Localize R2 for C1
name|tracker
operator|.
name|handle
argument_list|(
name|req21Event
argument_list|)
expr_stmt|;
name|dispatcher
operator|.
name|await
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|localizerEventHandler
argument_list|,
name|times
argument_list|(
literal|3
argument_list|)
argument_list|)
operator|.
name|handle
argument_list|(
name|any
argument_list|(
name|LocalizerResourceRequestEvent
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
comment|// Verify refCount for R1 is 2
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|lr1
operator|.
name|getRefCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// Verify refCount for R2 is 1
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|lr2
operator|.
name|getRefCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// Release R2 for C1
name|tracker
operator|.
name|handle
argument_list|(
name|rel21Event
argument_list|)
expr_stmt|;
name|dispatcher
operator|.
name|await
argument_list|()
expr_stmt|;
name|verifyTrackedResourceCount
argument_list|(
name|tracker
argument_list|,
literal|2
argument_list|)
expr_stmt|;
comment|// Verify resources in state INIT with ref-count=0 is removed.
name|Assert
operator|.
name|assertTrue
argument_list|(
name|tracker
operator|.
name|remove
argument_list|(
name|lr2
argument_list|,
name|mockDelService
argument_list|)
argument_list|)
expr_stmt|;
name|verifyTrackedResourceCount
argument_list|(
name|tracker
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// Verify resource with non zero ref count is not removed.
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|lr1
operator|.
name|getRefCount
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|tracker
operator|.
name|remove
argument_list|(
name|lr1
argument_list|,
name|mockDelService
argument_list|)
argument_list|)
expr_stmt|;
name|verifyTrackedResourceCount
argument_list|(
name|tracker
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// Localize resource1
name|ResourceLocalizedEvent
name|rle
init|=
operator|new
name|ResourceLocalizedEvent
argument_list|(
name|req1
argument_list|,
operator|new
name|Path
argument_list|(
literal|"file:///tmp/r1"
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|lr1
operator|.
name|handle
argument_list|(
name|rle
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|lr1
operator|.
name|getState
argument_list|()
operator|.
name|equals
argument_list|(
name|ResourceState
operator|.
name|LOCALIZED
argument_list|)
argument_list|)
expr_stmt|;
comment|// Release resource1
name|tracker
operator|.
name|handle
argument_list|(
name|rel11Event
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|handle
argument_list|(
name|rel12Event
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|lr1
operator|.
name|getRefCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// Verify resources in state LOCALIZED with ref-count=0 is removed.
name|Assert
operator|.
name|assertTrue
argument_list|(
name|tracker
operator|.
name|remove
argument_list|(
name|lr1
argument_list|,
name|mockDelService
argument_list|)
argument_list|)
expr_stmt|;
name|verifyTrackedResourceCount
argument_list|(
name|tracker
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|dispatcher
operator|!=
literal|null
condition|)
block|{
name|dispatcher
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testConsistency ()
specifier|public
name|void
name|testConsistency
parameter_list|()
block|{
name|String
name|user
init|=
literal|"testuser"
decl_stmt|;
name|DrainDispatcher
name|dispatcher
init|=
literal|null
decl_stmt|;
try|try
block|{
name|dispatcher
operator|=
name|createDispatcher
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|EventHandler
argument_list|<
name|LocalizerEvent
argument_list|>
name|localizerEventHandler
init|=
name|mock
argument_list|(
name|EventHandler
operator|.
name|class
argument_list|)
decl_stmt|;
name|EventHandler
argument_list|<
name|LocalizerEvent
argument_list|>
name|containerEventHandler
init|=
name|mock
argument_list|(
name|EventHandler
operator|.
name|class
argument_list|)
decl_stmt|;
name|dispatcher
operator|.
name|register
argument_list|(
name|LocalizerEventType
operator|.
name|class
argument_list|,
name|localizerEventHandler
argument_list|)
expr_stmt|;
name|dispatcher
operator|.
name|register
argument_list|(
name|ContainerEventType
operator|.
name|class
argument_list|,
name|containerEventHandler
argument_list|)
expr_stmt|;
name|ContainerId
name|cId1
init|=
name|BuilderUtils
operator|.
name|newContainerId
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|LocalizerContext
name|lc1
init|=
operator|new
name|LocalizerContext
argument_list|(
name|user
argument_list|,
name|cId1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|LocalResourceRequest
name|req1
init|=
name|createLocalResourceRequest
argument_list|(
name|user
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
name|LocalResourceVisibility
operator|.
name|PUBLIC
argument_list|)
decl_stmt|;
name|LocalizedResource
name|lr1
init|=
name|createLocalizedResource
argument_list|(
name|req1
argument_list|,
name|dispatcher
argument_list|)
decl_stmt|;
name|ConcurrentMap
argument_list|<
name|LocalResourceRequest
argument_list|,
name|LocalizedResource
argument_list|>
name|localrsrc
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|LocalResourceRequest
argument_list|,
name|LocalizedResource
argument_list|>
argument_list|()
decl_stmt|;
name|localrsrc
operator|.
name|put
argument_list|(
name|req1
argument_list|,
name|lr1
argument_list|)
expr_stmt|;
name|LocalResourcesTracker
name|tracker
init|=
operator|new
name|LocalResourcesTrackerImpl
argument_list|(
name|user
argument_list|,
name|dispatcher
argument_list|,
name|localrsrc
argument_list|)
decl_stmt|;
name|ResourceEvent
name|req11Event
init|=
operator|new
name|ResourceRequestEvent
argument_list|(
name|req1
argument_list|,
name|LocalResourceVisibility
operator|.
name|PUBLIC
argument_list|,
name|lc1
argument_list|)
decl_stmt|;
name|ResourceEvent
name|rel11Event
init|=
operator|new
name|ResourceReleaseEvent
argument_list|(
name|req1
argument_list|,
name|cId1
argument_list|)
decl_stmt|;
comment|// Localize R1 for C1
name|tracker
operator|.
name|handle
argument_list|(
name|req11Event
argument_list|)
expr_stmt|;
name|dispatcher
operator|.
name|await
argument_list|()
expr_stmt|;
comment|// Verify refCount for R1 is 1
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|lr1
operator|.
name|getRefCount
argument_list|()
argument_list|)
expr_stmt|;
name|dispatcher
operator|.
name|await
argument_list|()
expr_stmt|;
name|verifyTrackedResourceCount
argument_list|(
name|tracker
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// Localize resource1
name|ResourceLocalizedEvent
name|rle
init|=
operator|new
name|ResourceLocalizedEvent
argument_list|(
name|req1
argument_list|,
operator|new
name|Path
argument_list|(
literal|"file:///tmp/r1"
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|lr1
operator|.
name|handle
argument_list|(
name|rle
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|lr1
operator|.
name|getState
argument_list|()
operator|.
name|equals
argument_list|(
name|ResourceState
operator|.
name|LOCALIZED
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|createdummylocalizefile
argument_list|(
operator|new
name|Path
argument_list|(
literal|"file:///tmp/r1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|LocalizedResource
name|rsrcbefore
init|=
name|tracker
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|File
name|resFile
init|=
operator|new
name|File
argument_list|(
name|lr1
operator|.
name|getLocalPath
argument_list|()
operator|.
name|toUri
argument_list|()
operator|.
name|getRawPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|resFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|resFile
operator|.
name|delete
argument_list|()
argument_list|)
expr_stmt|;
comment|// Localize R1 for C1
name|tracker
operator|.
name|handle
argument_list|(
name|req11Event
argument_list|)
expr_stmt|;
name|dispatcher
operator|.
name|await
argument_list|()
expr_stmt|;
name|lr1
operator|.
name|handle
argument_list|(
name|rle
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|lr1
operator|.
name|getState
argument_list|()
operator|.
name|equals
argument_list|(
name|ResourceState
operator|.
name|LOCALIZED
argument_list|)
argument_list|)
expr_stmt|;
name|LocalizedResource
name|rsrcafter
init|=
name|tracker
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|rsrcbefore
operator|==
name|rsrcafter
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Localized resource should not be equal"
argument_list|)
expr_stmt|;
block|}
comment|// Release resource1
name|tracker
operator|.
name|handle
argument_list|(
name|rel11Event
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|dispatcher
operator|!=
literal|null
condition|)
block|{
name|dispatcher
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|createdummylocalizefile (Path path)
specifier|private
name|boolean
name|createdummylocalizefile
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
name|boolean
name|ret
init|=
literal|false
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|path
operator|.
name|toUri
argument_list|()
operator|.
name|getRawPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|ret
operator|=
name|file
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
DECL|method|verifyTrackedResourceCount (LocalResourcesTracker tracker, int expected)
specifier|private
name|void
name|verifyTrackedResourceCount
parameter_list|(
name|LocalResourcesTracker
name|tracker
parameter_list|,
name|int
name|expected
parameter_list|)
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
name|Iterator
argument_list|<
name|LocalizedResource
argument_list|>
name|iter
init|=
name|tracker
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Tracker resource count does not match"
argument_list|,
name|expected
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
DECL|method|createLocalResourceRequest (String user, int i, long ts, LocalResourceVisibility vis)
specifier|private
name|LocalResourceRequest
name|createLocalResourceRequest
parameter_list|(
name|String
name|user
parameter_list|,
name|int
name|i
parameter_list|,
name|long
name|ts
parameter_list|,
name|LocalResourceVisibility
name|vis
parameter_list|)
block|{
specifier|final
name|LocalResourceRequest
name|req
init|=
operator|new
name|LocalResourceRequest
argument_list|(
operator|new
name|Path
argument_list|(
literal|"file:///tmp/"
operator|+
name|user
operator|+
literal|"/rsrc"
operator|+
name|i
argument_list|)
argument_list|,
name|ts
operator|+
name|i
operator|*
literal|2000
argument_list|,
name|LocalResourceType
operator|.
name|FILE
argument_list|,
name|vis
argument_list|)
decl_stmt|;
return|return
name|req
return|;
block|}
DECL|method|createLocalizedResource (LocalResourceRequest req, Dispatcher dispatcher)
specifier|private
name|LocalizedResource
name|createLocalizedResource
parameter_list|(
name|LocalResourceRequest
name|req
parameter_list|,
name|Dispatcher
name|dispatcher
parameter_list|)
block|{
name|LocalizedResource
name|lr
init|=
operator|new
name|LocalizedResource
argument_list|(
name|req
argument_list|,
name|dispatcher
argument_list|)
decl_stmt|;
return|return
name|lr
return|;
block|}
DECL|method|createDispatcher (Configuration conf)
specifier|private
name|DrainDispatcher
name|createDispatcher
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|DrainDispatcher
name|dispatcher
init|=
operator|new
name|DrainDispatcher
argument_list|()
decl_stmt|;
name|dispatcher
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|dispatcher
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|dispatcher
return|;
block|}
block|}
end_class

end_unit

